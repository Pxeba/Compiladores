package src;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class ManipuladorDeArquivo {

    private BufferedReader leitor;
    private BufferedWriter escritor;

    public static ManipuladorDeArquivo instance = new ManipuladorDeArquivo();
    public static final String UTF8_BOM = "\uFEFF";

    void setup(String input, String output) {
        abrirArquivo(input);
//        criarArquivo(output);

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                salvar();
                super.run();
            }
        });
    }

    private void abrirArquivo(String arquivoInput) {
        try {
            InputStreamReader arq = new InputStreamReader(new FileInputStream(arquivoInput), StandardCharsets.UTF_8);
            leitor = new BufferedReader(arq);
        } catch(IOException e) {
            System.out.print("Erro na abertura do arquvivo: " + e.getMessage());
        }
    }

    private void criarArquivo(String arquivoOutput) {
        try {
            FileWriter arq = new FileWriter(arquivoOutput);
            escritor = new BufferedWriter(arq);
        } catch(IOException e) {
            System.out.print(e.getMessage());
        }
    }

    public String lerLinha() {
        try { return removeUTF8BOM(leitor.readLine()); } catch (IOException e) { return null; }
    }

    // correção manual necessaria segundo doc do java
    // https://www.rgagnon.com/javadetails/java-handle-utf8-file-with-bom.html
    private static String removeUTF8BOM(String s) {
        if (s != null  && s.startsWith(UTF8_BOM)) {
            s = s.substring(1);
        }
        return s;
    }

    public void escreverLinha(String linhaAssembly) {
        try { escritor.write(linhaAssembly); } catch (IOException e) { System.out.print(e.getMessage()); }
    }

    // fechar arquivos quando o objeto for retirada da memoria
    public void salvar() {
        try {
            leitor.close();
            if(escritor != null)
                escritor.close();
        } catch(IOException e) {
            System.out.print(e.getMessage());
        }
    }
}
