package jfx.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import jfx.Main;
import jfx.model.Payment;
import util.converter.DateConverter;
import util.DateUtil;
import util.converter.FloatConverter;
import util.parser.CsvParser;

import java.io.*;
import java.time.LocalDate;
import java.util.LinkedHashSet;

public class Controller {
    @FXML
    private TableView<Payment> paymentTable;
    @FXML
    private TableColumn<Payment, String> nameColumn;
    @FXML
    private TableColumn<Payment, String> paymentWayColumn;
    @FXML
    private TableColumn<Payment, Double> amountColumn;
    @FXML
    private TableColumn<Payment, LocalDate> dateColumn;
    @FXML
    private ToggleGroup payway;
    @FXML
    private TextField nameField;
    @FXML
    private DatePicker dateField;
    @FXML
    private Spinner<Double> amountField;
    @FXML
    private Button deleteButton;
    @FXML
    private TextArea chargeText;
    @FXML
    private MenuItem saveItem;
    @FXML
    private Menu files;

    private Main mainApp;
    private final FloatConverter stringConverter = new FloatConverter();
    private final DateConverter dateConverter = new DateConverter();
    private File file = null, directory = null;

    private final static double CHARGES = 0.25;
    private final static String CONFIG = "parametres.config";

    private double calcuatedCharges = 0;
    private double totalPayement = 0;

    public Controller() {
        hydrateDirectoryVariable();
    }

    @FXML
    private void initialize() {
        if (directory != null && directory.exists()) files.setDisable(false);
        paymentTable.setOnMouseClicked(event -> {
            int selectedIndex = paymentTable.getSelectionModel().getSelectedIndex();
            deleteButton.setDisable(selectedIndex == -1);
        });

        amountField.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0,1000.0,0.0, 0.1));

        nameColumn.setCellValueFactory(cellData -> cellData.getValue().getNameProperty());
        paymentWayColumn.setCellValueFactory(cellData -> cellData.getValue().getPaymentWayProperty());
        amountColumn.setCellValueFactory(cellData -> cellData.getValue().getAmountProperty().asObject());
        dateColumn.setCellValueFactory(cellData -> cellData.getValue().getDateProperty());

        setCellFactories();
        setCellOnEdit();
    }

    private void setCellFactories() {
        nameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        paymentWayColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        amountColumn.setCellFactory(TextFieldTableCell.forTableColumn(stringConverter));
        dateColumn.setCellFactory(TextFieldTableCell.forTableColumn(dateConverter));
    }

    private void setCellOnEdit() {
        paymentWayColumn.setOnEditCommit(event -> {
            int index = event.getTablePosition().getRow();
            String newValue = event.getNewValue();
            newValue = newValue.substring(0,1).toUpperCase() + newValue.substring(1);
            if (newValue.equals("Cheque") || newValue.equals("Espèces")) {
                event.getTableView().getItems().get(index).setPaymentWay(newValue);
            } else {
                mainApp.showAlert(Alert.AlertType.ERROR,
                        "Paiement invalide",
                        "Moyen de paiement non accepté",
                        "Les moyens de paiements autorisés sont les cheques et les espèces.");
                event.getTableView().getItems().get(index).setPaymentWay(event.getOldValue());
                event.getTableView().refresh();
            }
        });

        amountColumn.setOnEditCommit(event -> {
            int index = event.getTablePosition().getRow();
            double newValue = event.getNewValue();

            if (newValue < 0) {
                mainApp.showAlert(Alert.AlertType.ERROR,
                        "Montant invalide",
                        "Montant inférieur à 0",
                        "Il est impossible de recevoir un montant négatif.");
                event.getTableView().getItems().get(index).setAmount(event.getOldValue());
                event.getTableView().refresh();
            } else {
                event.getTableView().getItems().get(index).setAmount(newValue);
            }
        });

        dateColumn.setOnEditCommit(event -> {
            int index = event.getTablePosition().getRow();
            LocalDate newValue = event.getNewValue();
            if (newValue == null) {
                mainApp.showAlert(Alert.AlertType.ERROR,
                        "Date invalide",
                        "Cette date n'existe pas",
                        "La date que vous avez entré est invalide.");
                event.getTableView().getItems().get(index).setDate(DateUtil.format(event.getOldValue()));
                event.getTableView().refresh();
            } else {
                event.getTableView().getItems().get(index).setDate(DateUtil.format(newValue));
            }
        });

        nameColumn.setOnEditCommit(event -> {
            int index = event.getTablePosition().getRow();
            String newValue = event.getNewValue();
            event.getTableView().getItems().get(index).setName(newValue);
        });
    }

    public void setMainApp(Main app) {
        // On assigne la référence de l'application
        mainApp = app;

        // On ajoute les données de liste observable à la table
        paymentTable.setItems(mainApp.getPaymentData());
    }

    @FXML
    private void onDeletePayment() {
            int selectedIndex = paymentTable.getSelectionModel().getSelectedIndex();
            if (selectedIndex >= 0) {
                paymentTable.getItems().remove(selectedIndex);
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.initOwner(mainApp.getPrimaryStage());
                alert.setHeaderText("Pas de paiement séléctionné");
                alert.setContentText("Il faut séléctionner le paiement à supprimer");

                alert.showAndWait();
            }
    }

    @FXML
    private void onAddPayment() {
        if (amountField.getValue() < 0) {
            mainApp.showAlert(Alert.AlertType.WARNING,
                    "Montant invalide",
                    "Montant inférieur à 0",
                    "Il est impossible d'être payé d'un montant inférieur à 0");

        } else if (!DateUtil.isValid(dateField.getEditor().getText())) {
            mainApp.showAlert(Alert.AlertType.WARNING,
                    "Date invalide",
                    "La date est invalide",
                    "La date indiquée n'existe pas.");
        } else if (nameField.getText().equals("")) {
            mainApp.showAlert(Alert.AlertType.WARNING,
                    "Débitteur manquant",
                    "Pas de nom de débitteur",
                    "Il faut entrer un nom de débitteur à votre paiement.");
        } else {
            Payment payment = new Payment(nameField.getText(),
                    ((RadioButton) payway.getSelectedToggle()).getText(),
                    amountField.getValue(),
                    dateField.getEditor().getText());
            mainApp.getPaymentData().add(payment);
            updateChargeText();
        }
    }

    @FXML
    private void openFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Séléctionner un fichier CSV");
        if (directory == null || !directory.exists()) {
            fileChooser.setInitialDirectory(javax.swing.filechooser.FileSystemView.getFileSystemView().getHomeDirectory());
        } else {
            fileChooser.setInitialDirectory(directory);
        }
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Fichier CSV", "*.csv")
        );

        clearAll();
        file = fileChooser.showOpenDialog(mainApp.getPrimaryStage());

        if (file != null) {
            showCSV(file);

            mainApp.getPrimaryStage().setTitle("A déclarer - " + file.getName());
            saveItem.setDisable(false);
        } else {
            mainApp.getPrimaryStage().setTitle("A déclarer");
        }
        updateChargeText();
    }

    private void clearAll() {
        mainApp.getPaymentData().clear();
        paymentTable.getItems().clear();
        updateChargeText();
    }

    private void showCSV(File file) {
        CsvParser parser = new CsvParser();
        try {
            LinkedHashSet<Payment> payments = parser.parseFile(file);
            payments.forEach(this::addPayment);
        } catch (FileNotFoundException e) {
            mainApp.showAlert(Alert.AlertType.ERROR,
                    "Ficier inexistant",
                    "Le fichier " + file.getName() + " n'existe pas",
                    "Le fichier indiqué n'existe pas\n." +
                            "Vérifiez que vous ne l'avez pas supprimé avant de l'ouvrir.");
        } catch (IOException e) {
            mainApp.showAlert(Alert.AlertType.ERROR,
                    "Ficier vide",
                    "Le fichier " + file.getName() + " est vide",
                    "Le fichier indiqué est vide, ou est corrompu.");
        }
    }

    private void addPayment(Payment payment) {
        mainApp.getPaymentData().add(payment);
        updateChargeText();
    }

    @FXML
    private void saveAsCSV() {
        if (file == null) {
            mainApp.showAlert(Alert.AlertType.WARNING, "Ouvrir un fichier",
                    "Pas de fichier ouvert",
                    "Pour enregistrer les modification d'un fichier CSV" +
                            " il faut ouvrir le ficier, le modifier, puis enregistrer.");
        }
        CsvParser parser = new CsvParser();
        try {
            parser.editFile(file, mainApp.getPaymentData(), this.calcuatedCharges, this.totalPayement);
        } catch (IOException e) {
            mainApp.showAlert(Alert.AlertType.ERROR, "Fichier introuvable",
                    "Le fichier est introuvable",
                    "Le fichier que vous tentez d'éditer n'existe pas.\n" +
                            "Vérifiez que vous ne l'avez pas supprimé avant de tenter de sauvegarder");
        }
    }

    private void updateChargeText() {
        evaluateCharge();
        chargeText.setText("Total : " + this.totalPayement + " | Charges : " + this.calcuatedCharges);
    }

    @FXML
    private void askNewFile() {
        try {
            new AskNewFileController(this);
        } catch (IOException e) {
            mainApp.showAlert(Alert.AlertType.ERROR, "Erreur",
                    "Erreur pendant la création d'un nouveau fichier",
                    "Une erreur est survenue lors de la création du fichier");
            e.printStackTrace();
        }
    }

    public void createNewFile(String month, String year) {
        if (file != null) {
            saveAsCSV();
        }
        try {
            CsvParser parser = new CsvParser();
            if (directory != null) {
                System.out.println("Bon");
                file = parser.createFile(directory.getAbsolutePath(), month, year);
            }
            if (file == null) {
                mainApp.showAlert(Alert.AlertType.ERROR, "Fichier non crée",
                        "Le fichier n'a pas pu être crée",
                        "Le fichier ne peut pas être crée. Le chemin est peut être inaccessible," +
                                " ou un fichier porte déjà le même nom \"" + month + "_\"" + year + "\".");
            } else {
                mainApp.showAlert(Alert.AlertType.CONFIRMATION, "Fichier crée",
                        "Le fichier a été crée",
                        "Le fichier a été crée, et ouvert. Vous pouvez le modifier.");
                mainApp.getPaymentData().clear();
                mainApp.getPrimaryStage().setTitle("A déclarer - " + file.getName());
                saveItem.setDisable(false);
                updateChargeText();
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void chooseFolder() {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Choisir un dossier pour contenir les paiements");
        chooser.setInitialDirectory(javax.swing.filechooser.FileSystemView.getFileSystemView().getHomeDirectory());

        directory = chooser.showDialog(mainApp.getPrimaryStage());

        if (directory == null) {
            mainApp.showAlert(Alert.AlertType.ERROR, "Dossier introuvable",
                    "Dossier non trouvé ou inaccessible",
                    "Le dossier spécifié est introuvable ou inaccessible.");
        } else {
            createOrEditDirectoryFile();
            files.setDisable(false);
        }
    }

    private void createOrEditDirectoryFile() {
        File file = new File(CONFIG);
        try {
            file.createNewFile();
            FileWriter writer = new FileWriter(file);
            if (directory != null) {
                writer.write(directory.getAbsolutePath());
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void hydrateDirectoryVariable() {
        File file = new File(CONFIG);
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String path = reader.readLine();
            this.directory = (path == null) ? null : new File(path);
        } catch (IOException e) {
            createOrEditDirectoryFile();
        }
    }

    private void evaluateCharge() {
        double total = 0;
        for(Payment payment : mainApp.getPaymentData()) {
            total += payment.getAmount();
        }
        this.totalPayement = total;
        this.calcuatedCharges = total * CHARGES;
    }
}
