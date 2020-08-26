package util.parser;

import javafx.collections.ObservableList;
import jfx.model.Payment;
import util.DateUtil;

import java.io.*;
import java.util.LinkedHashSet;

public class CsvParser {
    private final static String COLUMN_NAMES = "Nom,Moyen de payement,montant,date";

    public void writeToFile(String fileName, String write) {
        try {
            FileWriter fw = new FileWriter(fileName);
            fw.write(write);
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public File createFile(String directory, String month, String year) throws IOException {
        String fileName = directory + "\\" + month + "_" + year + ".csv";
        try {
            System.out.println("directory : " + directory);
            File file = new File(fileName);
            System.out.println("File path : " + file.getAbsolutePath());
            System.out.println(file.getPath());
            if (file.createNewFile()) {
                System.out.println("Fichier crée");
                writeToFile(fileName, COLUMN_NAMES);
                return file;
            } else {
                System.out.println("Fichier pas crée");
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public void editFile(File file, ObservableList<Payment> payments, double charges, double total) throws IOException {
        FileWriter writer = new FileWriter(file.getAbsolutePath());

        writer.write(COLUMN_NAMES + "\n");

        // On utilise pas de lamba expression pour conserver le jet de l'exception dans cette fct
        for (Payment p : payments) {
            writer.write(p.getName() + "," +
                    p.getPaymentWay() + "," +
                    p.getAmount() + "," +
                    p.getDate().format(DateUtil.formatter) + "\n");
        }

        writer.write("\n" +
                "Total," + total + "\n" +
                "Charges," + charges);

        writer.close();
    }

    public LinkedHashSet<Payment> parseFile(File file) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        reader.readLine();
        String line;
        String[] datas;
        LinkedHashSet<Payment> payments = new LinkedHashSet<>();

        while ((line = reader.readLine()) != null) {
            datas = line.split(",");
            if (datas.length != 4) continue;
            System.out.println(datas[0]);
            Payment payment = new Payment(datas[0], datas[1], Double.parseDouble(datas[2]), datas[3]);
            payments.add(payment);
        }
        return payments;
    }
}
