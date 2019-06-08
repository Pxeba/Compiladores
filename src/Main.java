package src;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
//        Scanner scanner = new Scanner(System.in);
//
//        System.out.println("Nome arquivo input: ");
//        String in = scanner.nextLine();
//        System.out.println("Nome arquivo output: ");
//        String out = scanner.nextLine();

//        scanner.close();
        String fileIn = args[0];
        String fileOut = args[1];
        if(fileIn.contains(".l") && fileOut.contains(".asm"))
        ManipuladorDeArquivo.instance.setup(fileIn,fileOut);

        AnalisadorSintatico anSintatico = new AnalisadorSintatico();
        anSintatico.procS();

//        testar an lexico
//
//        AnalisadorLexico anLexico = new AnalisadorLexico();
//        Simbolo sim = anLexico.getToken();
//        while(sim != null) {
//            sim = anLexico.getToken();
//        }
    }
}
