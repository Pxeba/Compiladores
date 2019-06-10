import java.util.Collection;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/*
    Nome: Pedro Henrique Mattiello
    Matrícula: 524537
*/

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


class Error {
    public interface ErrorHandler {
        void handleError(ErrorTypes type, String lexema);
    }

    public enum ErrorTypes {
        CARACTERE_INVALIDO_LEXICO, LEXEMA_NAO_IDENTIFICADO_LEXICO, FIM_DE_ARQUIVO_NAO_ESPERADO_LEXICO,
        TOKEN_NAO_ESPERADO_SINTATICO, FIM_DE_ARQUIVO_NAO_ESPERADO_SINTATICO
    }
}

class AnalisadorLexico implements  Error.ErrorHandler {

    public int currentCharIndex;
    private String currentLine;
    public int currentLinePos;
    public String currentLexema = "";

    AnalisadorLexico() {
        currentCharIndex = 0;
        currentLine = ManipuladorDeArquivo.instance.lerLinha();
        currentLinePos = 1;
        TabelaDeSimbolo.instance.setup();
    }

    public static char[] simbolosValidos  = { ' ', '_', '.', ',', ';', '&', ':', '(',')','[',']','{','}','+','-','"','\'','/','%','^','@','!','?','>','<','=', '*', '\n', '\t'};

    private char validarCaractere(char c) {
        if(!Character.isLetter(c) && !Character.isDigit(c) && new String(simbolosValidos).indexOf(c) == -1) {
            handleError(Error.ErrorTypes.CARACTERE_INVALIDO_LEXICO, "");
        }
        return c;
    }

    private boolean isHexadecimal(char c) {
        return Character.isDigit(c) || c == 'A' || c == 'B' || c == 'C' || c == 'D' || c == 'E';
    }

    public Simbolo getToken() {
        int s = 0;
        currentLexema = "";
        byte currentLexemaIndex = 0;
        StringBuilder identificadorLexema = new StringBuilder();
        while(s != 9) {

            if(currentCharIndex >= currentLine.length() ) {
                currentCharIndex = 0;
                while( (currentLine = ManipuladorDeArquivo.instance.lerLinha()) != null && currentLine.length() == 0)
                    currentLinePos++;
                currentLinePos++;
                if(currentLine == null) {
                    if(s == 0 || s == 8) // Se estado de início ou comentário não finalizado
                        return null;
                    else
                        handleError(Error.ErrorTypes.FIM_DE_ARQUIVO_NAO_ESPERADO_LEXICO, "");
                }
            }
            char currentChar = validarCaractere(Character.toLowerCase(currentLine.charAt(currentCharIndex)));
            switch (s) {
                case 0:
                    currentLexema = "";
                    switch(currentChar) {
                        case ';': s = 9; currentLexemaIndex = TabelaDeSimbolo.Tokens.PONTO_E_VIRGULA.tByte; break;
                        case '(': s = 9; currentLexemaIndex = TabelaDeSimbolo.Tokens.PARENTESES_ABERTO.tByte; break;
                        case ')': s = 9; currentLexemaIndex = TabelaDeSimbolo.Tokens.PARENTESES_FECHADO.tByte; break;
                        case ',': s = 9; currentLexemaIndex = TabelaDeSimbolo.Tokens.VIRGULA.tByte; break;
                        case '{': s = 9; currentLexemaIndex = TabelaDeSimbolo.Tokens.ABRE_CHAVE.tByte; break;
                        case '<':
                            s = 9;
                            if(currentLine.charAt(currentCharIndex+1) == '=') { currentLexemaIndex = TabelaDeSimbolo.Tokens.MENOR_IGUAL.tByte; currentCharIndex++;}
                            else if(currentLine.charAt(currentCharIndex+1) == '>') { currentLexemaIndex = TabelaDeSimbolo.Tokens.DIFERENTE.tByte; currentCharIndex++;}
                            else { currentLexemaIndex = TabelaDeSimbolo.Tokens.MENOR_QUE.tByte; }
                            break;
                        case '+': s = 9; currentLexemaIndex = TabelaDeSimbolo.Tokens.SOMA.tByte; break;
                        case '}': s = 9; currentLexemaIndex = TabelaDeSimbolo.Tokens.FECHA_CHAVE.tByte; break;
                        case '%': s = 9; currentLexemaIndex = TabelaDeSimbolo.Tokens.PORCENTAGEM.tByte; break;
                        case '>':
                            s = 9;
                            if(currentLine.charAt(currentCharIndex+1) == '=') { currentLexemaIndex = TabelaDeSimbolo.Tokens.MAIOR_IGUAL.tByte; currentCharIndex++; }
                            else { currentLexemaIndex = TabelaDeSimbolo.Tokens.MAIOR_QUE.tByte; }
                            break;
                        case '-': s = 9; currentLexemaIndex = TabelaDeSimbolo.Tokens.MENOS.tByte; break;
                        case '[': s = 9; currentLexemaIndex = TabelaDeSimbolo.Tokens.ABRE_COLCHETE.tByte; break;
                        case '=': s = 9; currentLexemaIndex = TabelaDeSimbolo.Tokens.IGUAL.tByte; break;
                        case '*': s = 9; currentLexemaIndex = TabelaDeSimbolo.Tokens.MULTIPLICACAO.tByte; break;
                        case ']': s = 9; currentLexemaIndex = TabelaDeSimbolo.Tokens.FECHA_COLCHETE.tByte; break;
                        case '/':
                            if(currentLine.charAt(currentCharIndex+1) == '*') { s = 8; currentCharIndex++; }
                            else { s = 9; currentLexemaIndex = TabelaDeSimbolo.Tokens.BARRA.tByte; }
                            break;
                        case '\t':
                        case ' ': break;
                        case '"': s = 1; break;
                        case '\'': s = 2; break;
                        case '.': case '_':
                            identificadorLexema.append(currentChar);
                            s = 3;
                            break;
                        default:

                            // hexadecimal
                            if(Character.isDigit(currentChar)) { if(currentChar == '0') { s = 6;} else { s = 4; } }

                            // identificadores | palavras reservadas
                            else if(Character.isLetter(currentChar)) {
                                s = 5;
                                identificadorLexema.append(currentChar);
                            }

                            else {  handleError(Error.ErrorTypes.LEXEMA_NAO_IDENTIFICADO_LEXICO, ""+currentChar); }

                            break;
                    }
                    break;
                case 1:
                    if(currentChar == '\n' || currentChar == '$') { handleError(Error.ErrorTypes.LEXEMA_NAO_IDENTIFICADO_LEXICO, ""+currentChar); }
                    if(currentChar == '"') { s = 9; currentLexemaIndex = TabelaDeSimbolo.Tokens.KVALUE.tByte; }
                    break;
                case 2:
                    if(currentChar == '\'') { s = 9; currentLexemaIndex = TabelaDeSimbolo.Tokens.KVALUE.tByte; }
                    else { s = 7; }
                    break;
                case 3:
                    if(Character.isDigit(currentChar) || Character.isLetter(currentChar) ||
                        currentChar == '.' || currentChar == '_') { s = 5; identificadorLexema.append(currentChar);}
                    else { handleError(Error.ErrorTypes.LEXEMA_NAO_IDENTIFICADO_LEXICO, ""+currentChar); }
                    break;
                case 4: // digitos constante
                    // verificar se proximos digitos podem ser id

                    if(!Character.isDigit(currentChar)) {
                        s = 9; currentLexemaIndex = TabelaDeSimbolo.Tokens.KVALUE.tByte; currentCharIndex--;
                    }
                    break;
                case 5:
                    if( (!Character.isDigit(currentChar) && !Character.isLetter(currentChar) &&
                            currentChar != '.' && currentChar != '_') || (currentChar == ' ' || currentChar == '\n' || currentChar == ';')  ) {
                        s = 9;
                        currentLexemaIndex = TabelaDeSimbolo.instance.buscarToken(identificadorLexema.toString()).tByte;
                        currentCharIndex--;
                    } else {
                        identificadorLexema.append(currentChar);
                        for (int i = currentCharIndex + 1; i < currentLine.length(); i++) {
                            char currentCharScope = currentLine.charAt(i);

                            if (currentCharScope == '\n' || currentCharScope == ' ' || currentCharScope == ';' || (!Character.isLetter(currentCharScope) &&
                                    !Character.isDigit((currentCharScope)) && currentCharScope != '.' && currentCharScope != '_')) { // se delimitador, inserir indentificador
                                s = 9;
                                currentLexemaIndex = TabelaDeSimbolo.instance.buscarToken(identificadorLexema.toString()).tByte;
                                break;
                            } else {
                                identificadorLexema.append(Character.toLowerCase(currentCharScope));
                            }

                            currentCharIndex = i;
                        }
                    }

                    break;
                case 6:
                    if(currentChar == 'x' && currentCharIndex+2 <= currentLine.length()) {

                        // valida se é um character hexadecimal
                        if(isHexadecimal(currentLine.charAt(currentCharIndex+1)) &&
                            isHexadecimal(currentLine.charAt(currentCharIndex+2))) {
                            s = 9;
                            currentLexemaIndex = TabelaDeSimbolo.Tokens.KVALUE.tByte;
                            currentCharIndex = currentCharIndex + 2;
                        }
                        else { handleError(Error.ErrorTypes.LEXEMA_NAO_IDENTIFICADO_LEXICO, ""+currentChar); }
                    }
                    else
                        if (Character.isDigit(currentChar)) { s = 4;}
                        else { s = 9; currentLexemaIndex = TabelaDeSimbolo.Tokens.KVALUE.tByte; currentCharIndex--; }
                    break;

                case 7:
                    if(currentChar == '\'') { s = 9; currentLexemaIndex = TabelaDeSimbolo.Tokens.KVALUE.tByte;}
                    else { handleError(Error.ErrorTypes.LEXEMA_NAO_IDENTIFICADO_LEXICO, ""+currentChar); }
                    break;

                case 8:
                    if(currentChar == '*')
                        if(currentCharIndex < currentLine.length())
                            if(currentLine.charAt(currentCharIndex+1) == '/') {
                                s = 0;
                                currentCharIndex++;
                            }
                    break;

            }
            currentCharIndex++;
            currentLexema += currentChar;
        }
        //System.out.print(TabelaDeSimbolo.instance.tabela.get(TabelaDeSimbolo.Tokens.getByValue(currentLexemaIndex)).lexema + " ");
        return TabelaDeSimbolo.instance.tabela.get(TabelaDeSimbolo.Tokens.getByValue(currentLexemaIndex));
    }

    @Override
    public void handleError(Error.ErrorTypes type, String lexema) {
        switch(type) {
            case CARACTERE_INVALIDO_LEXICO:
                System.out.println(currentLinePos + ":" + "caractere invalido.");
                break;
            case LEXEMA_NAO_IDENTIFICADO_LEXICO:
                System.out.println(currentLinePos + ":" + "lexema nao identificado [" + lexema + "].");
                break;
            case FIM_DE_ARQUIVO_NAO_ESPERADO_LEXICO:
                System.out.println(currentLinePos + ":" + "fim de arquivo nao esperado.");
                break;
        }
        System.exit(1);
    }
}




class AnalisadorSintatico implements Error.ErrorHandler {

    private TabelaDeSimbolo.Tokens tokenAtual;
    private Simbolo simboloAtual;
    private AnalisadorLexico anLexico = new AnalisadorLexico();


    private boolean isFirstDeD() {
        return contemTokenAtual(new TabelaDeSimbolo.Tokens[] { TabelaDeSimbolo.Tokens.VAR, TabelaDeSimbolo.Tokens.CONST,
                 TabelaDeSimbolo.Tokens.MENOS, TabelaDeSimbolo.Tokens.KVALUE });
    }

    private boolean isFirstDeC() {
        return contemTokenAtual(new TabelaDeSimbolo.Tokens[] {  TabelaDeSimbolo.Tokens.ID, TabelaDeSimbolo.Tokens.FOR, TabelaDeSimbolo.Tokens.IF,
                TabelaDeSimbolo.Tokens.PONTO_E_VIRGULA, TabelaDeSimbolo.Tokens.READLN, TabelaDeSimbolo.Tokens.WRITE, TabelaDeSimbolo.Tokens.WRITELN });
    }

    private boolean isFirstDeL() {
        return contemTokenAtual(new TabelaDeSimbolo.Tokens[] {  TabelaDeSimbolo.Tokens.INTEGER, TabelaDeSimbolo.Tokens.CHAR});
    }

    private boolean isFirstDeB() {
        return contemTokenAtual(new TabelaDeSimbolo.Tokens[] {  TabelaDeSimbolo.Tokens.CONST, TabelaDeSimbolo.Tokens.MENOS,
                TabelaDeSimbolo.Tokens.KVALUE});
    }

    private boolean isFirstDeF() {
        return contemTokenAtual(new TabelaDeSimbolo.Tokens[] {TabelaDeSimbolo.Tokens.ID, TabelaDeSimbolo.Tokens.KVALUE,
                TabelaDeSimbolo.Tokens.NOT, TabelaDeSimbolo.Tokens.PARENTESES_ABERTO});
    }

    private void casaToken(TabelaDeSimbolo.Tokens tokenEsperado) {
        if(tokenAtual == null) { handleError(Error.ErrorTypes.FIM_DE_ARQUIVO_NAO_ESPERADO_SINTATICO, null); }

        if(tokenAtual == tokenEsperado) {
            simboloAtual = anLexico.getToken();
            if(simboloAtual == null )
                tokenAtual = null;
            else
                tokenAtual = TabelaDeSimbolo.Tokens.getByValue(simboloAtual.tByte);
        } else {
//            handleError(tokenEsperado.name());
              handleError(Error.ErrorTypes.TOKEN_NAO_ESPERADO_SINTATICO, simboloAtual.lexema);
        }
    }

    public void procS() {

        simboloAtual = anLexico.getToken();
        if(simboloAtual != null )
            tokenAtual = TabelaDeSimbolo.Tokens.getByValue(simboloAtual.tByte);

        while(isFirstDeD()) {
            procD();
        }

        while(isFirstDeC()) {
            procC();
        }

        if(simboloAtual != null)
            handleError(Error.ErrorTypes.TOKEN_NAO_ESPERADO_SINTATICO, simboloAtual.lexema);

    }

    private void procD() {
        if(tokenAtual == TabelaDeSimbolo.Tokens.VAR) {
            casaToken(tokenAtual);
            do { procL(); } while (isFirstDeL());
        }

        else
            while(isFirstDeB()) { procB();}

    }

    private void procL() {

//        Simbolo.Tipos flagTipo = Simbolo.Tipos.INT ; // Flag para indicar o tipo do identificador
//        int flagMenos = 0; // Flag para indicar o se o menos foi declarado
//        int flagVetor = 0; // Flag para indicar de o Identificador declarado e' um vetor

        if(!isFirstDeL()) {
//            handleError("firstDeL");
              handleError(Error.ErrorTypes.TOKEN_NAO_ESPERADO_SINTATICO, simboloAtual.lexema);
        }

        if(tokenAtual == TabelaDeSimbolo.Tokens.INTEGER || tokenAtual == TabelaDeSimbolo.Tokens.CHAR) {
            //[1] [2]
//            flagTipo = tokenAtual == TabelaDeSimbolo.Tokens.INTEGER ? Simbolo.Tipos.INT : Simbolo.Tipos.CHAR;
            casaToken(tokenAtual);
        }
        else
             handleError(Error.ErrorTypes.TOKEN_NAO_ESPERADO_SINTATICO, simboloAtual.lexema);
//           handleError("int ou char");


//        int posID = pos;

//        Collection<Simbolo> simbolos = TabelaDeSimbolo.instance.tabela.values();
//        //[3]
//        for( Simbolo s: simbolos ) {
//            if (s.lexema.toLowerCase().equals(simboloAtual.lexema) && s.tByte == simboloAtual.tByte) {
////                handleError(Error.ErrorTypes.FIM_DE_ARQUIVO_NAO_ESPERADO_LEXICO, "null");
//            }
//        }
//        simboloAtual.tipo = flagTipo;
//        simboloAtual.tamanho = 0;
//        simboloAtual.classe = Simbolo.Classes.VAR;

        casaToken(TabelaDeSimbolo.Tokens.ID);
        procX(Simbolo.Tipos.INT, simboloAtual);

        while(tokenAtual == TabelaDeSimbolo.Tokens.VIRGULA) {
            casaToken(tokenAtual);
            casaToken(TabelaDeSimbolo.Tokens.ID);

            procX(Simbolo.Tipos.INT, simboloAtual);
        }
        casaToken(TabelaDeSimbolo.Tokens.PONTO_E_VIRGULA);
    }

    private void procB() {
//        Simbolo.Tipos flagtipo = Simbolo.Tipos.INT;
//        // [4]
//        int flagMenos = 0;

        casaToken(TabelaDeSimbolo.Tokens.CONST);
//        Simbolo sId = simboloAtual;
        casaToken(TabelaDeSimbolo.Tokens.ID);

        // [9]
//        Collection<Simbolo> simbolos = TabelaDeSimbolo.instance.tabela.values();
//        for( Simbolo s: simbolos ) {
//            if (s.lexema.toLowerCase().equals(simboloAtual.lexema) && s.tByte == simboloAtual.tByte) {
////                handleError(Error.ErrorTypes.FIM_DE_ARQUIVO_NAO_ESPERADO_LEXICO, "null");
//            }
//        }
//
//        simboloAtual.tamanho = 0;
//        simboloAtual.classe = Simbolo.Classes.CONST;

        casaToken(TabelaDeSimbolo.Tokens.IGUAL);
        if(tokenAtual == TabelaDeSimbolo.Tokens.MENOS) {
            casaToken(tokenAtual);
            // [5]
//            flagMenos = 1;
        }

//        if(flagMenos == 1 && getTipo(anLexico.currentLexema) != Simbolo.Tipos.INT) {
////            handleError(Error.ErrorTypes.FIM_DE_ARQUIVO_NAO_ESPERADO_LEXICO, null);
//        }

//        sId.tipo = getTipo(anLexico.currentLexema);
//
//        if(anLexico.currentLexema.startsWith("\"")) {
//            sId.tamanho = anLexico.currentLexema.length();
//        }

//        if(getTipo(anLexico.currentLexema) == Simbolo.Tipos.INT) {
//            int valK = 0;
//            try { valK = Integer.parseInt(anLexico.currentLexema); }
//            catch ( Exception e ) {
////                handleError(Error.ErrorTypes.FIM_DE_ARQUIVO_NAO_ESPERADO_LEXICO, null);
//            }
//
//            if(simboloAtual.tipo == Simbolo.Tipos.INT)
//                if(valK < -32768 || valK > 32767) {
////                    handleError(Error.ErrorTypes.FIM_DE_ARQUIVO_NAO_ESPERADO_LEXICO, null);
//                }
//        } else if(getTipo(anLexico.currentLexema) == Simbolo.Tipos.CHAR) {
//            int valK = 0;
//            try { valK = Integer.parseInt(anLexico.currentLexema); }
//            catch ( Exception e ) {
////                handleError(Error.ErrorTypes.FIM_DE_ARQUIVO_NAO_ESPERADO_LEXICO, null);
//            }
//
//            if(valK > 255) {
////                handleError(Error.ErrorTypes.FIM_DE_ARQUIVO_NAO_ESPERADO_LEXICO, null);
//            }
//        }

        casaToken(TabelaDeSimbolo.Tokens.KVALUE);
        casaToken(TabelaDeSimbolo.Tokens.PONTO_E_VIRGULA);
    }

    private void procC() {
        if(!isFirstDeC()) {
              handleError(Error.ErrorTypes.TOKEN_NAO_ESPERADO_SINTATICO, simboloAtual.lexema);
//            handleError("firstDeC");
        }


        if(tokenAtual == TabelaDeSimbolo.Tokens.ID) {
            casaToken(TabelaDeSimbolo.Tokens.ID);
            if(tokenAtual == TabelaDeSimbolo.Tokens.ABRE_COLCHETE) {
                casaToken(tokenAtual);
                procExp();
                casaToken(TabelaDeSimbolo.Tokens.FECHA_COLCHETE);
            }

            casaToken(TabelaDeSimbolo.Tokens.IGUAL);
            procExp();
            casaToken(TabelaDeSimbolo.Tokens.PONTO_E_VIRGULA);
            return;
        }


        if(tokenAtual == TabelaDeSimbolo.Tokens.FOR) {
            casaToken(TabelaDeSimbolo.Tokens.FOR);
            casaToken(TabelaDeSimbolo.Tokens.ID);
            casaToken(TabelaDeSimbolo.Tokens.IGUAL);
            procExp();
            casaToken(TabelaDeSimbolo.Tokens.TO);
            procExp();

            if(tokenAtual == TabelaDeSimbolo.Tokens.STEP) {
                casaToken(tokenAtual);
                procExp();
            }

            casaToken(TabelaDeSimbolo.Tokens.DO);

            if(isFirstDeC()) {
                procC();
            } else {
                casaToken(TabelaDeSimbolo.Tokens.ABRE_CHAVE);
                while(isFirstDeC())
                    procC();

                casaToken(TabelaDeSimbolo.Tokens.FECHA_CHAVE);
            }
            return;
        }


        if(tokenAtual == TabelaDeSimbolo.Tokens.IF) {
            casaToken(tokenAtual);
            procExp();
            casaToken(TabelaDeSimbolo.Tokens.THEN);
            procY();
            if(tokenAtual == TabelaDeSimbolo.Tokens.ELSE) {
                casaToken(tokenAtual);
                procY();
            }
            return;
        }


        if(tokenAtual == TabelaDeSimbolo.Tokens.PONTO_E_VIRGULA) {
            casaToken(tokenAtual);
            return;
        }


        if(tokenAtual == TabelaDeSimbolo.Tokens.READLN) {
            casaToken(tokenAtual);
            casaToken(TabelaDeSimbolo.Tokens.PARENTESES_ABERTO);
            casaToken(TabelaDeSimbolo.Tokens.ID);
            casaToken(TabelaDeSimbolo.Tokens.PARENTESES_FECHADO);
            casaToken(TabelaDeSimbolo.Tokens.PONTO_E_VIRGULA);
            return;
        }


        if(tokenAtual == TabelaDeSimbolo.Tokens.WRITE) {
            casaToken(tokenAtual);
            procZ();
            return;
        }


        if(tokenAtual == TabelaDeSimbolo.Tokens.WRITELN) {
            casaToken(tokenAtual);
            procZ();
            return;
        }


    }

    private void procX(Simbolo.Tipos flagTipo, Simbolo sId) {
        // [4]
        int flagMenos = 0;
        int flagVetor = 0;
        if (tokenAtual == TabelaDeSimbolo.Tokens.IGUAL) {
            casaToken(tokenAtual);
            if (tokenAtual == TabelaDeSimbolo.Tokens.MENOS) {
                casaToken(tokenAtual);
//                // [5]
//                flagMenos = 1;
            }

//            // [7]
//            if ( flagMenos == 1 && getTipo(anLexico.currentLexema)!= Simbolo.Tipos.INT )
//            {
////                handleError(Error.ErrorTypes.FIM_DE_ARQUIVO_NAO_ESPERADO_LEXICO, null);
//            }
//
//            // [6]
//            if(flagTipo != getTipo(anLexico.currentLexema))
//                if(getTipo(anLexico.currentLexema) != Simbolo.Tipos.INT || flagTipo != Simbolo.Tipos.CHAR) {
////                    handleError(Error.ErrorTypes.FIM_DE_ARQUIVO_NAO_ESPERADO_LEXICO, null);
//                }
//
//             if(getTipo(anLexico.currentLexema) == Simbolo.Tipos.INT) {
//                if(flagTipo == Simbolo.Tipos.INT) {
//                    if(Integer.parseInt(anLexico.currentLexema) < -32768|| Integer.parseInt(anLexico.currentLexema) > 32767) {
////                        handleError(Error.ErrorTypes.FIM_DE_ARQUIVO_NAO_ESPERADO_LEXICO, null);
//                    }
//                }
//
//                 else if(flagTipo == Simbolo.Tipos.CHAR) {
//                     if(Integer.parseInt(anLexico.currentLexema) < -128|| Integer.parseInt(anLexico.currentLexema) > 127) {
////                         handleError(Error.ErrorTypes.FIM_DE_ARQUIVO_NAO_ESPERADO_LEXICO, null);
//                     }
//                 }
//             } else if(flagTipo == Simbolo.Tipos.CHAR) {
//                  if((int) (anLexico.currentLexema.charAt(0)) > 255) {
////                     handleError(Error.ErrorTypes.FIM_DE_ARQUIVO_NAO_ESPERADO_LEXICO, null);
//                  }
//             }

            casaToken(TabelaDeSimbolo.Tokens.KVALUE);
        }
        else if(tokenAtual == TabelaDeSimbolo.Tokens.ABRE_COLCHETE) {
//            flagVetor = 1;
//            sId.tamanho = 1;
            casaToken(tokenAtual);

//            int valK = 0;
//            try { valK = Integer.parseInt(anLexico.currentLexema); }
//            catch ( Exception e ) {
////                handleError(Error.ErrorTypes.FIM_DE_ARQUIVO_NAO_ESPERADO_LEXICO, null);
//            }
//
//            if(valK <= 0) {
////                handleError(Error.ErrorTypes.FIM_DE_ARQUIVO_NAO_ESPERADO_LEXICO, null);
//            }
//
//            if ( ( valK > 2000 && simboloAtual.tipo == Simbolo.Tipos.INT) || ( valK > 4000 && simboloAtual.tipo == Simbolo.Tipos.CHAR) ) {
////                handleError(Error.ErrorTypes.FIM_DE_ARQUIVO_NAO_ESPERADO_LEXICO, null);
//            }
//
//            simboloAtual.tamanho = valK;


            casaToken(TabelaDeSimbolo.Tokens.KVALUE);
            casaToken(TabelaDeSimbolo.Tokens.FECHA_COLCHETE);
        }

    }

    private void procY() {
        if(isFirstDeC()) {
            procC();
        } else {
            casaToken(TabelaDeSimbolo.Tokens.ABRE_CHAVE);
            while(isFirstDeC())
                procC();
            casaToken(TabelaDeSimbolo.Tokens.FECHA_CHAVE);
        }
    }

    private void procZ() {
        casaToken(TabelaDeSimbolo.Tokens.PARENTESES_ABERTO);
        procExp();
        while (tokenAtual == TabelaDeSimbolo.Tokens.VIRGULA) {
            casaToken(tokenAtual);
            procExp();
        }
        casaToken(TabelaDeSimbolo.Tokens.PARENTESES_FECHADO);
        casaToken(TabelaDeSimbolo.Tokens.PONTO_E_VIRGULA);
    }

    private void procExp() {
        procExps();
        if(contemTokenAtual(new TabelaDeSimbolo.Tokens[] { TabelaDeSimbolo.Tokens.IGUAL, TabelaDeSimbolo.Tokens.DIFERENTE, TabelaDeSimbolo.Tokens.MENOR_QUE,
                TabelaDeSimbolo.Tokens.MAIOR_QUE, TabelaDeSimbolo.Tokens.MENOR_IGUAL, TabelaDeSimbolo.Tokens.MAIOR_QUE, TabelaDeSimbolo.Tokens.MAIOR_IGUAL})) {
            casaToken(tokenAtual);
            procExps();
        }
    }

    private void procExps() {
        if(contemTokenAtual(new TabelaDeSimbolo.Tokens[] {TabelaDeSimbolo.Tokens.SOMA, TabelaDeSimbolo.Tokens.MENOS}))
            casaToken(tokenAtual);

        procT();
        while(contemTokenAtual(new TabelaDeSimbolo.Tokens[] {TabelaDeSimbolo.Tokens.SOMA, TabelaDeSimbolo.Tokens.MENOS, TabelaDeSimbolo.Tokens.OR} )) {
            casaToken(tokenAtual);
            procT();
        }
    }

    private void procT() {
        procF();
        while(contemTokenAtual(new TabelaDeSimbolo.Tokens[] {TabelaDeSimbolo.Tokens.MULTIPLICACAO, TabelaDeSimbolo.Tokens.BARRA,
                TabelaDeSimbolo.Tokens.AND, TabelaDeSimbolo.Tokens.PORCENTAGEM})) {
            casaToken(tokenAtual);
            procF();
        }
    }

    private void procF() {
        if(!isFirstDeF()) {
              handleError(Error.ErrorTypes.TOKEN_NAO_ESPERADO_SINTATICO, simboloAtual.lexema);
//            handleError("firstDeF");
        }


        if(tokenAtual == TabelaDeSimbolo.Tokens.ID) {
            casaToken(tokenAtual);
            if (tokenAtual == TabelaDeSimbolo.Tokens.ABRE_COLCHETE) {
                casaToken(tokenAtual);
                procExp();
                casaToken(TabelaDeSimbolo.Tokens.FECHA_COLCHETE);
            }
            return;
        }


        if(tokenAtual == TabelaDeSimbolo.Tokens.KVALUE) {
            casaToken(tokenAtual);
            return;
        }

        if(tokenAtual == TabelaDeSimbolo.Tokens.NOT) {
            casaToken(tokenAtual);
            procF();
            return;
        }


        if(tokenAtual == TabelaDeSimbolo.Tokens.PARENTESES_ABERTO) {
            casaToken(tokenAtual);
            procExp();
            casaToken(TabelaDeSimbolo.Tokens.PARENTESES_FECHADO);
            return;
        }

    }

    private Simbolo.Tipos getTipo(String KVALUE) {
        if(KVALUE.startsWith("'"))
            return Simbolo.Tipos.CHAR;
        else
            if(KVALUE.startsWith("\""))
                return Simbolo.Tipos.CHAR;
            else
                return Simbolo.Tipos.INT;
    }

    private boolean contemTokenAtual(TabelaDeSimbolo.Tokens[] tokens) {
        for(TabelaDeSimbolo.Tokens token: tokens)
            if(tokenAtual == token)
                return true;

        return false;
    }

    @Override
    public void handleError(Error.ErrorTypes type, String lexema) {
        switch(type) {
            case TOKEN_NAO_ESPERADO_SINTATICO:
                System.out.println(anLexico.currentLinePos + ":token nao esperado [" + lexema + "].");
                break;
            case FIM_DE_ARQUIVO_NAO_ESPERADO_SINTATICO:
                System.out.println("fim de arquivo nao esperado.");
                break;
        }
        System.exit(1);
    }

}



class ManipuladorDeArquivo {

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

class Simbolo {
   byte tByte;
   String lexema;
   Classes classe;
   Tipos tipo;
   int tamanho;

   enum Tipos {
      INT,CHAR,BOOL
   }

   enum Classes {
      VAR,CONST
   }
   
   Simbolo(byte tByte, String lexema) { 
      this.tByte = tByte;
      this.lexema = lexema;
   }
}




class TabelaDeSimbolo { 

   Map<Tokens,Simbolo> tabela;
   int lastIndex = 0;
   
   public enum Tokens {
      CHAR(0), INTEGER(1), CONST(2),ELSE(3), PARENTESES_ABERTO(4), MENOR_IGUAL(5),PONTO_E_VIRGULA(6),WRITE(7),VAR(8), AND(9),
      PARENTESES_FECHADO(10), VIRGULA(11), ABRE_CHAVE(12), WRITELN(13), OR(14),MENOR_QUE(15),SOMA(16),FECHA_CHAVE(17), PORCENTAGEM(18),
      NOT(19),MAIOR_QUE(20),MENOS(21),THEN(22),ABRE_COLCHETE(23),FOR(24),IGUAL(25),DIFERENTE(26),MULTIPLICACAO(27),READLN(28),FECHA_COLCHETE(29),
      IF(30),TO(31),MAIOR_IGUAL(32),BARRA(33),STEP(34),DO(35), ID(36), KVALUE(37);

      public final byte tByte;
      private static final Map<Byte , Tokens> TokensMap = new HashMap<>();

      static {
         for (Tokens tokens : values()) {
            TokensMap.put(tokens.tByte, tokens);
         }
      }

      public static Tokens getByValue(byte value) {
         return TokensMap.get(value);
      }

      Tokens(int tByte) {
         this.tByte = (byte) tByte;
      }
   }

   public static TabelaDeSimbolo instance = new TabelaDeSimbolo();
   
   private TabelaDeSimbolo() {
      tabela = new HashMap<Tokens,Simbolo>();
   }
   
   public void setup() { 
      tabela.put(Tokens.CHAR, new Simbolo(Tokens.CHAR.tByte,Tokens.CHAR.name()));
      tabela.put(Tokens.INTEGER, new Simbolo(Tokens.INTEGER.tByte,Tokens.INTEGER.name()));
      tabela.put(Tokens.CONST, new Simbolo(Tokens.CONST.tByte,Tokens.CONST.name()));
      tabela.put(Tokens.ELSE, new Simbolo(Tokens.ELSE.tByte,Tokens.ELSE.name()));
      tabela.put(Tokens.PARENTESES_ABERTO, new Simbolo(Tokens.PARENTESES_ABERTO.tByte,"("));
      tabela.put(Tokens.MENOR_IGUAL, new Simbolo(Tokens.MENOR_IGUAL.tByte,"<="));
      tabela.put(Tokens.PONTO_E_VIRGULA, new Simbolo(Tokens.PONTO_E_VIRGULA.tByte,";"));
      tabela.put(Tokens.WRITE, new Simbolo(Tokens.WRITE.tByte,Tokens.WRITE.name()));
      tabela.put(Tokens.VAR, new Simbolo(Tokens.VAR.tByte,Tokens.VAR.name()));
      tabela.put(Tokens.AND, new Simbolo(Tokens.AND.tByte,Tokens.AND.name()));
      tabela.put(Tokens.PARENTESES_FECHADO, new Simbolo(Tokens.PARENTESES_FECHADO.tByte,")"));
      tabela.put(Tokens.VIRGULA, new Simbolo(Tokens.VIRGULA.tByte,","));
      tabela.put(Tokens.ABRE_CHAVE, new Simbolo(Tokens.ABRE_CHAVE.tByte,"{"));
      tabela.put(Tokens.WRITELN, new Simbolo(Tokens.WRITELN.tByte,Tokens.WRITELN.name()));
      tabela.put(Tokens.OR, new Simbolo(Tokens.OR.tByte,Tokens.OR.name()));
      tabela.put(Tokens.MENOR_QUE, new Simbolo(Tokens.MENOR_QUE.tByte,"<"));
      tabela.put(Tokens.SOMA, new Simbolo(Tokens.SOMA.tByte,"+"));
      tabela.put(Tokens.FECHA_CHAVE, new Simbolo(Tokens.FECHA_CHAVE.tByte,"}"));
      tabela.put(Tokens.PORCENTAGEM, new Simbolo(Tokens.PORCENTAGEM.tByte,"%"));
      tabela.put(Tokens.NOT, new Simbolo(Tokens.NOT.tByte,Tokens.NOT.name()));
      tabela.put(Tokens.MAIOR_QUE, new Simbolo(Tokens.MAIOR_QUE.tByte,">"));
      tabela.put(Tokens.MENOS, new Simbolo(Tokens.MENOS.tByte,"-"));
      tabela.put(Tokens.THEN, new Simbolo(Tokens.THEN.tByte,Tokens.THEN.name()));
      tabela.put(Tokens.ABRE_COLCHETE, new Simbolo(Tokens.ABRE_COLCHETE.tByte,"["));
      tabela.put(Tokens.FOR, new Simbolo(Tokens.FOR.tByte,Tokens.FOR.name()));
      tabela.put(Tokens.IGUAL, new Simbolo(Tokens.IGUAL.tByte,"="));
      tabela.put(Tokens.DIFERENTE, new Simbolo(Tokens.DIFERENTE.tByte,"<>"));
      tabela.put(Tokens.MULTIPLICACAO, new Simbolo(Tokens.MULTIPLICACAO.tByte,"*"));
      tabela.put(Tokens.READLN, new Simbolo(Tokens.READLN.tByte,Tokens.READLN.name()));
      tabela.put(Tokens.FECHA_COLCHETE, new Simbolo(Tokens.FECHA_COLCHETE.tByte,"]"));
      tabela.put(Tokens.IF, new Simbolo(Tokens.IF.tByte,Tokens.IF.name()));
      tabela.put(Tokens.TO, new Simbolo(Tokens.TO.tByte,Tokens.TO.name()));
      tabela.put(Tokens.MAIOR_IGUAL, new Simbolo(Tokens.MAIOR_IGUAL.tByte,">="));
      tabela.put(Tokens.BARRA, new Simbolo(Tokens.BARRA.tByte,"/"));
      tabela.put(Tokens.STEP, new Simbolo(Tokens.STEP.tByte,Tokens.STEP.name()));
      tabela.put(Tokens.DO, new Simbolo(Tokens.DO.tByte,Tokens.DO.name()));
      tabela.put(Tokens.KVALUE, new Simbolo(Tokens.KVALUE.tByte,Tokens.KVALUE.name()));
   }

   public Simbolo buscarToken(String lexema) {
      Tokens token = null;
      Collection<Simbolo> simbolos = tabela.values();
      for( Simbolo s: simbolos ) {
         if (s.lexema.toLowerCase().equals(lexema)) {
            token = Tokens.getByValue(s.tByte);
            break;
         }
      }

      if(token == null) { // nenhum token encontrado, logo identificador
          inserirIdentificador(lexema);
          return tabela.get(Tokens.ID);
      }
     else {
          return tabela.get(token);
      }
   }

   public int inserirIdentificador(String id) {
      tabela.put(Tokens.ID, new Simbolo(Tokens.ID.tByte, id));
      return tabela.size();
   }

//   public void testarTabela() {
//       for(int i=0;i<tabela.size();i++) {
//            System.out.println(tabela.get(i).lexema);
//       }
//   }
}


