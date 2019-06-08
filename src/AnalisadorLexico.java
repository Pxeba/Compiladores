package src;

public class AnalisadorLexico implements  Error.ErrorHandler {

    public int currentCharIndex;
    private String currentLine;
    public int currentLinePos;

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
        byte currentLexemaIndex = 0;
        StringBuilder identificadorLexema = new StringBuilder();
        while(s != 9) {

            if(currentCharIndex >= currentLine.length() ) {
                currentCharIndex = 0;
                while( (currentLine = ManipuladorDeArquivo.instance.lerLinha()) != null && currentLine.length() == 0)
                    currentLinePos++;
                currentLinePos++;
                System.out.println();
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
        }
        System.out.print(TabelaDeSimbolo.instance.tabela.get(TabelaDeSimbolo.Tokens.getByValue(currentLexemaIndex)).lexema + " ");
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
