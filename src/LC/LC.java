package LC;

/*
        Pedro Henrique Mattiello
        524537
 */

import java.util.Scanner;

public class LC {

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
        if(fileIn.endsWith(".l") && fileOut.endsWith(".asm")) {
            ManipuladorDeArquivo.instance.setup(fileIn, fileOut);

            AnalisadorSintatico anSintatico = new AnalisadorSintatico();
            anSintatico.procS();
        }

//        Testar Analisador Lexico
//
//        AnalisadorLexico anLexico = new AnalisadorLexico();
//        Simbolo sim = anLexico.getToken();
//        while(sim != null) {
//            sim = anLexico.getToken();
//        }
    }
}
