package src;

public class AnalisadorSintatico implements Error.ErrorHandler {

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
        System.out.println("S begin");

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

        System.out.println("S end");
    }

    private void procD() {
        System.out.println("D begin");

        if(tokenAtual == TabelaDeSimbolo.Tokens.VAR) {
            casaToken(tokenAtual);
            do { procL(); } while (isFirstDeL());
        }

        else
            while(isFirstDeB()) { procB();}

        System.out.println("D end");
    }

    private void procL() {
        System.out.println("L begin");
        if(!isFirstDeL()) {
//            handleError("firstDeL");
              handleError(Error.ErrorTypes.TOKEN_NAO_ESPERADO_SINTATICO, simboloAtual.lexema);
        }

        if(tokenAtual == TabelaDeSimbolo.Tokens.INTEGER || tokenAtual == TabelaDeSimbolo.Tokens.CHAR)
            casaToken(tokenAtual);
        else
              handleError(Error.ErrorTypes.TOKEN_NAO_ESPERADO_SINTATICO, simboloAtual.lexema);
//            handleError("int ou char");

        casaToken(TabelaDeSimbolo.Tokens.ID);
        procX();

        while(tokenAtual == TabelaDeSimbolo.Tokens.VIRGULA) {
            casaToken(tokenAtual);
            casaToken(TabelaDeSimbolo.Tokens.ID);

            procX();
        }
        casaToken(TabelaDeSimbolo.Tokens.PONTO_E_VIRGULA);
        System.out.println("L end");
    }

    private void procB() {
        System.out.println("B begin");
        casaToken(TabelaDeSimbolo.Tokens.CONST);
        casaToken(TabelaDeSimbolo.Tokens.ID);
        casaToken(TabelaDeSimbolo.Tokens.IGUAL);
        if(tokenAtual == TabelaDeSimbolo.Tokens.MENOS)
            casaToken(tokenAtual);
        casaToken(TabelaDeSimbolo.Tokens.KVALUE);
        casaToken(TabelaDeSimbolo.Tokens.PONTO_E_VIRGULA);
        System.out.println("B end");
    }

    private void procC() {
        System.out.println("C begin");
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
            System.out.println("C end");
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
            System.out.println("C end");
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
            System.out.println("C end");
            return;
        }


        if(tokenAtual == TabelaDeSimbolo.Tokens.PONTO_E_VIRGULA) {
            casaToken(tokenAtual);
            System.out.println("C end");
            return;
        }


        if(tokenAtual == TabelaDeSimbolo.Tokens.READLN) {
            casaToken(tokenAtual);
            casaToken(TabelaDeSimbolo.Tokens.PARENTESES_ABERTO);
            casaToken(TabelaDeSimbolo.Tokens.ID);
            casaToken(TabelaDeSimbolo.Tokens.PARENTESES_FECHADO);
            casaToken(TabelaDeSimbolo.Tokens.PONTO_E_VIRGULA);
            System.out.println("C end");
            return;
        }


        if(tokenAtual == TabelaDeSimbolo.Tokens.WRITE) {
            casaToken(tokenAtual);
            procZ();
            System.out.println("C end");
            return;
        }


        if(tokenAtual == TabelaDeSimbolo.Tokens.WRITELN) {
            casaToken(tokenAtual);
            procZ();
            System.out.println("C end");
            return;
        }


    }

    private void procX() {
        System.out.println("X begin");
        if (tokenAtual == TabelaDeSimbolo.Tokens.IGUAL) {
            casaToken(tokenAtual);
            if (tokenAtual == TabelaDeSimbolo.Tokens.MENOS)
                casaToken(tokenAtual);
            casaToken(TabelaDeSimbolo.Tokens.KVALUE);
        }
        else if(tokenAtual == TabelaDeSimbolo.Tokens.ABRE_COLCHETE){
            casaToken(tokenAtual);
            casaToken(TabelaDeSimbolo.Tokens.KVALUE);
            casaToken(TabelaDeSimbolo.Tokens.FECHA_COLCHETE);
        }

        System.out.println("X end");
    }

    private void procY() {
        System.out.println("Y begin");
        if(isFirstDeC()) {
            procC();
        } else {
            casaToken(TabelaDeSimbolo.Tokens.ABRE_CHAVE);
            while(isFirstDeC())
                procC();
            casaToken(TabelaDeSimbolo.Tokens.FECHA_CHAVE);
        }
        System.out.println("Y end");
    }

    private void procZ() {
        System.out.println("Z begin");
        casaToken(TabelaDeSimbolo.Tokens.PARENTESES_ABERTO);
        procExp();
        while (tokenAtual == TabelaDeSimbolo.Tokens.VIRGULA) {
            casaToken(tokenAtual);
            procExp();
        }
        casaToken(TabelaDeSimbolo.Tokens.PARENTESES_FECHADO);
        casaToken(TabelaDeSimbolo.Tokens.PONTO_E_VIRGULA);
        System.out.println("Z end");
    }

    private void procExp() {
        System.out.println("Exp begin");
        procExps();
        if(contemTokenAtual(new TabelaDeSimbolo.Tokens[] { TabelaDeSimbolo.Tokens.IGUAL, TabelaDeSimbolo.Tokens.DIFERENTE, TabelaDeSimbolo.Tokens.MENOR_QUE,
                TabelaDeSimbolo.Tokens.MAIOR_QUE, TabelaDeSimbolo.Tokens.MENOR_IGUAL, TabelaDeSimbolo.Tokens.MAIOR_QUE, TabelaDeSimbolo.Tokens.MAIOR_IGUAL})) {
            casaToken(tokenAtual);
            procExps();
        }
        System.out.println("Exp end");
    }

    private void procExps() {
        System.out.println("Exps begin");
        if(contemTokenAtual(new TabelaDeSimbolo.Tokens[] {TabelaDeSimbolo.Tokens.SOMA, TabelaDeSimbolo.Tokens.MENOS}))
            casaToken(tokenAtual);

        procT();
        while(contemTokenAtual(new TabelaDeSimbolo.Tokens[] {TabelaDeSimbolo.Tokens.SOMA, TabelaDeSimbolo.Tokens.MENOS, TabelaDeSimbolo.Tokens.OR} )) {
            casaToken(tokenAtual);
            procT();
        }
        System.out.println("Exps end");
    }

    private void procT() {
        System.out.println("T begin");
        procF();
        while(contemTokenAtual(new TabelaDeSimbolo.Tokens[] {TabelaDeSimbolo.Tokens.MULTIPLICACAO, TabelaDeSimbolo.Tokens.BARRA,
                TabelaDeSimbolo.Tokens.AND, TabelaDeSimbolo.Tokens.PORCENTAGEM})) {
            casaToken(tokenAtual);
            procF();
        }
        System.out.println("T end");
    }

    private void procF() {
        System.out.println("F begin");
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

        System.out.println("F end");
    }

    private boolean contemTokenAtual(TabelaDeSimbolo.Tokens[] tokens) {
        for(TabelaDeSimbolo.Tokens token: tokens)
            if(tokenAtual == token)
                return true;

        return false;
    }

//    public void handleError(String tokenEsperado) {
//        System.out.println("token_atual = " + tokenAtual + " token_esperado = " + tokenEsperado);
//        System.out.println(anLexico.currentLinePos);
//        System.exit(1);
//    }

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
