package LC;

import java.util.Collection;

import static LC.TabelaDeSimbolo.Tokens.*;

public class AnalisadorSintatico implements Error.ErrorHandler {

    private TabelaDeSimbolo.Tokens tokenAtual;
    private Simbolo simboloAtual;
    private AnalisadorLexico anLexico = new AnalisadorLexico();


    private boolean isFirstDeD() {
        return contemTokenAtual(new TabelaDeSimbolo.Tokens[] { VAR, CONST,
                 MENOS, KVALUE });
    }

    private boolean isFirstDeC() {
        return contemTokenAtual(new TabelaDeSimbolo.Tokens[] {  ID, FOR, IF,
                PONTO_E_VIRGULA, READLN, WRITE, WRITELN });
    }

    private boolean isFirstDeL() {
        return contemTokenAtual(new TabelaDeSimbolo.Tokens[] {  INTEGER, CHAR});
    }

    private boolean isFirstDeB() {
        return contemTokenAtual(new TabelaDeSimbolo.Tokens[] {  CONST, MENOS,
                KVALUE});
    }

    private boolean isFirstDeF() {
        return contemTokenAtual(new TabelaDeSimbolo.Tokens[] {ID, KVALUE,
                NOT, PARENTESES_ABERTO});
    }

    private void casaToken(TabelaDeSimbolo.Tokens tokenEsperado) {
        if(tokenAtual == null) { handleError(Error.ErrorTypes.FIM_DE_ARQUIVO_NAO_ESPERADO, null); }

        if(tokenAtual == tokenEsperado) {
            simboloAtual = anLexico.getToken();
            if(simboloAtual == null ) {
                tokenAtual = null;
            }
            else {
                tokenAtual = getByValue(simboloAtual.tByte);
            }
        } else {
//            handleError(tokenEsperado.name());
              handleError(Error.ErrorTypes.TOKEN_NAO_ESPERADO_SINTATICO, simboloAtual.lexema);
        }
    }

    void procS() {

        simboloAtual = anLexico.getToken();
        if(simboloAtual != null ) {
            tokenAtual = getByValue(simboloAtual.tByte);
        }

        while(isFirstDeD()) {
            procD();
        }

        while(isFirstDeC()) {
            procC();
        }

        if(simboloAtual != null) {
            handleError(Error.ErrorTypes.TOKEN_NAO_ESPERADO_SINTATICO, simboloAtual.lexema);
        }
    }

    private void procD() {
        if(tokenAtual == VAR) {
            casaToken(VAR);
            do { procL(); } while (isFirstDeL());
        }

        else {
            while (isFirstDeB()) {
                procB();
            }
        }

    }

    private void procL() {

        if(!isFirstDeL()) {
//            handleError("firstDeL");
            handleError(Error.ErrorTypes.TOKEN_NAO_ESPERADO_SINTATICO, simboloAtual.lexema);
        }

        Simbolo.Tipos flagTipo = Simbolo.Tipos.INT ;

        if(tokenAtual == INTEGER || tokenAtual == CHAR) {
            //[1] [2]
            flagTipo = tokenAtual == INTEGER ? Simbolo.Tipos.INT : Simbolo.Tipos.CHAR;
            casaToken(tokenAtual);
        }
        else {
            handleError(Error.ErrorTypes.TOKEN_NAO_ESPERADO_SINTATICO, simboloAtual.lexema);
//           handleError("int ou char");
        }

        verificarUnicidade(simboloAtual);
//        Collection<Simbolo> simbolos = TabelaDeSimbolo.instance.tabela.values();
//        //[3]
//        for( Simbolo s: simbolos ) {
//            if (s.lexema.toLowerCase().equals(simboloAtual.lexema) && s.tByte == simboloAtual.tByte) {
//                handleError(Error.ErrorTypes.ID_JA_DECLARADO_SEMANTICO, simboloAtual.lexema);
//            }
//        }

        simboloAtual.tipo = flagTipo;
        simboloAtual.tamanho = 0;
        simboloAtual.classe = Simbolo.Classes.VAR;
        Simbolo simboloTmp = simboloAtual;

        casaToken(ID);
        procX(simboloTmp);

        while(tokenAtual == VIRGULA) {
            casaToken(VIRGULA);

            verificarUnicidade(simboloAtual);


            simboloAtual.tipo = flagTipo;
            simboloAtual.tamanho = 0;
            simboloAtual.classe = Simbolo.Classes.VAR;
            simboloTmp = simboloAtual;

            casaToken(ID);

            procX(simboloTmp);
        }
        casaToken(PONTO_E_VIRGULA);
    }

    private void procB() {
        Simbolo.Tipos flagtipo = Simbolo.Tipos.INT;
        // [4]
        int flagMenos = 0;

        casaToken(CONST);
        Simbolo tmpSimbolo = simboloAtual;
        // [9]
        verificarUnicidade(tmpSimbolo);


        casaToken(ID);

        tmpSimbolo.tamanho = 0;
        tmpSimbolo.classe = Simbolo.Classes.CONST;

        casaToken(IGUAL);
        if(tokenAtual == MENOS) {
            casaToken(MENOS);
            // [5]
            flagMenos = 1;
        }

        if(flagMenos == 1 && (getTipo(anLexico.currentLexema) != Simbolo.Tipos.INT)) {
            handleError(Error.ErrorTypes.TIPOS_INCOMPATIVEIS_SEMANTICO, null);
        }

        tmpSimbolo.tipo = getTipo(anLexico.currentLexema);
        tmpSimbolo.valor = (flagMenos == 1) ? "-" : "";
        tmpSimbolo.valor += anLexico.currentLexema;

        if(anLexico.currentLexema.startsWith("\"")) {
            tmpSimbolo.tamanho = anLexico.currentLexema.length();
        }

        casaToken(KVALUE);
        casaToken(PONTO_E_VIRGULA);
    }

    private void procC() {
        if(!isFirstDeC()) {
              handleError(Error.ErrorTypes.TOKEN_NAO_ESPERADO_SINTATICO, simboloAtual.lexema);
//            handleError("firstDeC");
        }

        ExpressaoInfo tmpExp;
        int flagPosVet = 0;
        Simbolo tmpSimbolo = simboloAtual;

        if(tokenAtual == ID) {
            verificarDeclaracaoPrevia(tmpSimbolo);

            if(tmpSimbolo.classe != Simbolo.Classes.VAR) {
                handleError(Error.ErrorTypes.CLASSE_DE_ID_INCOMPATIVEL_SEMANTICO, anLexico.currentLexema);
            }

            casaToken(ID);

            if(tokenAtual == ABRE_COLCHETE) {
                if(tmpSimbolo.tamanho == 0) {
                    handleError(Error.ErrorTypes.TIPOS_INCOMPATIVEIS_SEMANTICO, null);
                }

                casaToken(ABRE_COLCHETE);
                tmpExp = procExp();
                checkExpInteira(tmpExp);

                int valK = 0;
                try { valK = Integer.parseInt(tmpExp.valor); }
                catch ( Exception e ) {
                    e.printStackTrace();
//                handleError(Error.ErrorTypes.TAMANHO_MAXIMO_EXCEDIDO_SEMANTICO, null);
                }

                if(valK < 0) {
                    handleError(Error.ErrorTypes.TIPOS_INCOMPATIVEIS_SEMANTICO, null);
                }

                else if ( ( valK > 2000 && tmpSimbolo.tipo == Simbolo.Tipos.INT ) || ( valK > 4000 && tmpSimbolo.tipo == Simbolo.Tipos.CHAR) ) {
                    handleError(Error.ErrorTypes.TAMANHO_MAXIMO_EXCEDIDO_SEMANTICO, null);
                }


                flagPosVet = 1;
                casaToken(FECHA_COLCHETE);
            }

            casaToken(IGUAL);
            tmpExp = procExp();
            if(tmpSimbolo.tipo != tmpExp.tipo) {
                handleError(Error.ErrorTypes.TIPOS_INCOMPATIVEIS_SEMANTICO, null);
            } else {
                if(tmpSimbolo.isArrayDeCharacter() && flagPosVet == 0) {
                    if(tmpSimbolo.tamanho < tmpExp.tamanho - 1) {
                        handleError(Error.ErrorTypes.TAMANHO_MAXIMO_EXCEDIDO_SEMANTICO, null);
                    }
                }

                else if(tmpSimbolo.isArrayDeCharacter() && flagPosVet == 1) {
                    checkExpCharacter(tmpExp);
                }

                else if(tmpSimbolo.isInteiro()) {
                    checkExpInteira(tmpExp);
                }

                else if(tmpSimbolo.isArrayDeInteiro() && flagPosVet == 0) {
                    handleError(Error.ErrorTypes.TIPOS_INCOMPATIVEIS_SEMANTICO, null);
                }

                else if(tmpSimbolo.isArrayDeInteiro() && flagPosVet == 1) {
                    checkExpInteira(tmpExp);
                }
            }

            tmpSimbolo.valor = tmpExp.valor;
            casaToken(PONTO_E_VIRGULA);
            return;
        }




        if(tokenAtual == FOR) {
            casaToken(FOR);

            casaToken(ID);

            casaToken(IGUAL);
            tmpExp = procExp();
            checkExpInteira(tmpExp);

            casaToken(TO);
            tmpExp = procExp();

            if(tmpExp == null) {
                handleError(Error.ErrorTypes.ID_NAO_DECLARADO_SEMANTICO, tmpExp.valor);
            }

            checkExpInteira(tmpExp);

            if(tokenAtual == STEP) {
                casaToken(STEP);
                tmpExp = procExp();
                checkExpInteira(tmpExp);
            }

            casaToken(DO);

            if(isFirstDeC()) {
                procC();
            } else {
                casaToken(ABRE_CHAVE);
                while(isFirstDeC())
                    procC();

                casaToken(FECHA_CHAVE);
            }
            return;
        }




        if(tokenAtual == IF) {
            casaToken(IF);
            tmpExp = procExp();
            checkExpLogica(tmpExp);
            casaToken(THEN);
            procY();
            if(tokenAtual == ELSE) {
                casaToken(ELSE);
                procY();
            }
            return;
        }





        if(tokenAtual == PONTO_E_VIRGULA) {
            casaToken(PONTO_E_VIRGULA);
            return;
        }




        if(tokenAtual == READLN) {
            casaToken(READLN);
            casaToken(PARENTESES_ABERTO);
            casaToken(ID);
            casaToken(PARENTESES_FECHADO);
            casaToken(PONTO_E_VIRGULA);
            return;
        }




        if(tokenAtual == WRITE) {
            casaToken(WRITE);
            procZ();
            return;
        }




        if(tokenAtual == WRITELN) {
            casaToken(WRITELN);
            procZ();
            return;
        }


    }

    private void procX(Simbolo tmpSimbolo) {
        // [4]
        int flagMenos = 0;
        int flagVetor = 0;

        if (tokenAtual == IGUAL) {
            casaToken(IGUAL);
            if (tokenAtual == MENOS) {
                casaToken(MENOS);
//                // [5]
                flagMenos = 1;
            }

            // [7]
            if ( flagMenos == 1 && getTipo(anLexico.currentLexema) != Simbolo.Tipos.INT ) {
                handleError(Error.ErrorTypes.TIPOS_INCOMPATIVEIS_SEMANTICO, null);
            }

            // [6]
            if(tmpSimbolo.tipo != getTipo(anLexico.currentLexema)) {
                handleError(Error.ErrorTypes.TIPOS_INCOMPATIVEIS_SEMANTICO, null);

            }

            tmpSimbolo.valor = anLexico.currentLexema;
            casaToken(KVALUE);
        }
        else if(tokenAtual == ABRE_COLCHETE) {
            flagVetor = 1;
            casaToken(ABRE_COLCHETE);

            int valK = 0;
            try { valK = Integer.parseInt(anLexico.currentLexema); }
            catch ( Exception e ) {
                e.printStackTrace();
//                handleError(Error.ErrorTypes.TAMANHO_MAXIMO_EXCEDIDO_SEMANTICO, null);
            }

            if(valK < 0) {
                handleError(Error.ErrorTypes.TIPOS_INCOMPATIVEIS_SEMANTICO, null);
            }

            else if ( ( valK > 2000 && tmpSimbolo.tipo == Simbolo.Tipos.INT) || ( valK > 4000 && tmpSimbolo.tipo == Simbolo.Tipos.CHAR) ) {
                handleError(Error.ErrorTypes.TAMANHO_MAXIMO_EXCEDIDO_SEMANTICO, null);
            }

            tmpSimbolo.tamanho = valK;

            casaToken(KVALUE);
            casaToken(FECHA_COLCHETE);
        }

    }

    private void procY() {
        if(isFirstDeC()) {
            procC();
        } else {
            casaToken(ABRE_CHAVE);
            while(isFirstDeC())
                procC();
            casaToken(FECHA_CHAVE);
        }
    }

    private void procZ() {
        casaToken(PARENTESES_ABERTO);
        procExp();
        while (tokenAtual == VIRGULA) {
            casaToken(VIRGULA);
            procExp();
        }
        casaToken(PARENTESES_FECHADO);
        casaToken(PONTO_E_VIRGULA);
    }

    private ExpressaoInfo procExp() {
        ExpressaoInfo exp1;
        ExpressaoInfo exp2;
        TabelaDeSimbolo.Tokens tokOperador;

        exp1 = procExps();
        if(contemTokenAtual(new TabelaDeSimbolo.Tokens[] { IGUAL, DIFERENTE, MENOR_QUE,
                MAIOR_QUE, MENOR_IGUAL, MAIOR_QUE, MAIOR_IGUAL})) {
            tokOperador = tokenAtual;
            casaToken(tokenAtual);
            exp2 = procExps();
            verificadorDeTiposDeOperacao(exp1,exp2,tokOperador);
        }
        return  exp1;
    }

    private ExpressaoInfo procExps() {
        int flagOperacao = 0;
        TabelaDeSimbolo.Tokens tokOperador;
        ExpressaoInfo exp1;
        ExpressaoInfo exp2;

        if(contemTokenAtual(new TabelaDeSimbolo.Tokens[] {SOMA, MENOS})) {
            casaToken(tokenAtual);
            flagOperacao = 1;
        }

        exp1 = procT();
        if(flagOperacao == 1) {
            checkExpInteira(exp1);
        }

        while(contemTokenAtual(new TabelaDeSimbolo.Tokens[] {SOMA, MENOS, OR} )) {
            tokOperador = tokenAtual;
            casaToken(tokenAtual);
            exp2 = procT();

            if(tokOperador == SOMA || tokOperador == MENOS ) {
                if((exp1.tipo == exp2.tipo) && exp1.tamanho == 0 && exp2.tamanho == 0){ // não pode ser String, apenas char e int
                    if(exp1.isCharacter()) {
                        checkExpCharacter(exp1);
                        checkExpCharacter(exp2);
                    } else {
                        checkExpInteira(exp1);
                        checkExpInteira(exp2);
                    }
                } else {
                    handleError(Error.ErrorTypes.TIPOS_INCOMPATIVEIS_SEMANTICO, null);
                }
            } else {
                checkExpLogica(exp1);
                checkExpLogica(exp2);
            }
            verificadorDeTiposDeOperacao(exp1,exp2,tokOperador);
        }
        return exp1;
    }

    private ExpressaoInfo procT() {
        ExpressaoInfo exp1;
        ExpressaoInfo exp2;
        TabelaDeSimbolo.Tokens tokOperador;
        exp1 = procF();

        while(contemTokenAtual(new TabelaDeSimbolo.Tokens[] {MULTIPLICACAO, BARRA,
                AND, PORCENTAGEM})) {
            tokOperador = tokenAtual;
            casaToken(tokenAtual);
            exp2 = procF();

            if(tokOperador == MULTIPLICACAO || tokOperador == BARRA || tokOperador ==  PORCENTAGEM) {
                checkExpInteira(exp1);
                checkExpInteira(exp2);
            } else {
                checkExpLogica(exp1);
                checkExpLogica(exp2);
            }
            verificadorDeTiposDeOperacao(exp1,exp2,tokOperador);
        }
        return exp1;
    }

    private ExpressaoInfo procF() {
        if(!isFirstDeF()) {
            handleError(Error.ErrorTypes.TOKEN_NAO_ESPERADO_SINTATICO, simboloAtual.lexema);
        }

        ExpressaoInfo tmpExp;

        if(tokenAtual == ID) {
            Simbolo simboloTmp = simboloAtual;
            verificarDeclaracaoPrevia(simboloAtual);

            casaToken(ID);
            if (tokenAtual == ABRE_COLCHETE) {
                if(!simboloTmp.isArrayDeCharacter() && !simboloTmp.isArrayDeInteiro()) {
                    handleError(Error.ErrorTypes.TIPOS_INCOMPATIVEIS_SEMANTICO, null);
                }

                casaToken(ABRE_COLCHETE);
                tmpExp = procExp();
                checkExpInteira(tmpExp);
                casaToken(FECHA_COLCHETE);

                return new ExpressaoInfo(simboloTmp.tipo, simboloTmp.valor, 0); // array[k] -> int
            }

            if(simboloTmp.isCharacter()) {
                 return new ExpressaoInfo(simboloTmp.tipo, simboloTmp.valor, 0);
            }

            else if(simboloTmp.isInteiro()) {
                return new ExpressaoInfo(simboloTmp.tipo, simboloTmp.valor, 0);
            }

            else {
                if(simboloTmp.isArrayDeCharacter()) {
                    return new ExpressaoInfo(simboloTmp.tipo, simboloTmp.valor, simboloTmp.tamanho);
                } else if(simboloTmp.isArrayDeInteiro()) {
                    return new ExpressaoInfo(simboloTmp.tipo, simboloTmp.valor, simboloTmp.tamanho);
                } else {
                    handleError(Error.ErrorTypes.ID_NAO_DECLARADO_SEMANTICO, simboloTmp.lexema);
                }
            }
        }


        else if(tokenAtual == KVALUE) {
            String lexemaK = anLexico.currentLexema;
            casaToken(KVALUE);

            if(getTipo(lexemaK) == Simbolo.Tipos.CHAR && lexemaK.startsWith("\"")) {
                return new ExpressaoInfo(getTipo(lexemaK), lexemaK, lexemaK.length());
            }

            else if(getTipo(lexemaK) == Simbolo.Tipos.CHAR) {
                return new ExpressaoInfo(getTipo(lexemaK), lexemaK, 0);
            }

            else if(getTipo(lexemaK) == Simbolo.Tipos.INT) {
                int valK = Integer.MIN_VALUE;
                try { valK = Integer.parseInt(lexemaK); }
                catch ( Exception e ){
//                    handleError(Error.ErrorTypes.TAMANHO_MAXIMO_EXCEDIDO_SEMANTICO, null);
                }
                return new ExpressaoInfo(getTipo(lexemaK), Integer.toString(valK), 0);
            }
        }


        else if(tokenAtual == NOT) {
            casaToken(NOT);
            tmpExp = procF();
            tmpExp.negar();
            return tmpExp;
        } else {
            casaToken(PARENTESES_ABERTO);
            tmpExp = procExp();
            casaToken(PARENTESES_FECHADO);
            return tmpExp;
        }

        handleError(Error.ErrorTypes.TIPOS_INCOMPATIVEIS_SEMANTICO, null);
        return null;
    }

    private void verificarUnicidade(Simbolo sId) {
//        if( TabelaDeSimbolo.instance.tabela.containsValue(id)) {
//            handleError(Error.ErrorTypes.ID_JA_DECLARADO_SEMANTICO, anLexico.currentLexema);
//        }
        Collection<Simbolo> simbolos = TabelaDeSimbolo.instance.tabela.values();
        for( Simbolo s: simbolos ) {
            if (s.lexema.toLowerCase().equals(sId.lexema) && s.classe != null) {
                    handleError(Error.ErrorTypes.ID_JA_DECLARADO_SEMANTICO, sId.lexema);
            }
        }
    }

    private void verificarDeclaracaoPrevia(Simbolo sId) {
        Collection<Simbolo> simbolos = TabelaDeSimbolo.instance.tabela.values();
        for( Simbolo s: simbolos ) {
            if (s.lexema.toLowerCase().equals(sId.lexema) && s.classe == null) {
                handleError(Error.ErrorTypes.ID_NAO_DECLARADO_SEMANTICO, sId.lexema);
            }
        }
    }

    public void checkExpInteira(ExpressaoInfo exp)
    {
        if ( ! exp.isInteiro()  )
            handleError(Error.ErrorTypes.TIPOS_INCOMPATIVEIS_SEMANTICO, null);
    }

    public void checkExpLogica(ExpressaoInfo exp)
    {
        if ( ! exp.isLogico()  )
            handleError(Error.ErrorTypes.TIPOS_INCOMPATIVEIS_SEMANTICO, null);
    }

    public void checkExpCharacter(ExpressaoInfo exp)
    {
        if ( !exp.isCharacter()  )
            handleError(Error.ErrorTypes.TIPOS_INCOMPATIVEIS_SEMANTICO, null);
    }

    private Simbolo.Tipos getTipo(String KVALUE) {
        if(KVALUE.startsWith("'")) {
            return Simbolo.Tipos.CHAR;
        }
        else {
            if (KVALUE.startsWith("\"")) {
                return Simbolo.Tipos.CHAR;
            }
            else {
                if(KVALUE.startsWith("0x")) {
                    return Simbolo.Tipos.CHAR;
                } else {
                    return Simbolo.Tipos.INT;
                }
            }
        }
    }

    TabelaDeSimbolo.Tokens[] opAritmeticas = new TabelaDeSimbolo.Tokens[] {MULTIPLICACAO, BARRA, PORCENTAGEM, SOMA, MENOS, IGUAL, DIFERENTE,
            MENOR_QUE, MAIOR_QUE, MENOR_IGUAL, MAIOR_IGUAL};
    TabelaDeSimbolo.Tokens[] opAritmeticasInteiras = new TabelaDeSimbolo.Tokens[] { MENOS, BARRA, MULTIPLICACAO, PORCENTAGEM };
    TabelaDeSimbolo.Tokens[] opAritmeticasLogicas = new TabelaDeSimbolo.Tokens[] { IGUAL, DIFERENTE, MENOR_QUE, MAIOR_QUE, MENOR_IGUAL, MAIOR_IGUAL};

    public void verificadorDeTiposDeOperacao(ExpressaoInfo exp1, ExpressaoInfo exp2, TabelaDeSimbolo.Tokens tokOpr) {
        if(contemTokenOp(opAritmeticas, tokOpr)) {
            operacaoAritmetica(exp1,exp2, tokOpr);
        } else {
            operacaoLogica(exp1,exp2, tokOpr);
        }
    }

    public void operacaoAritmetica(ExpressaoInfo exp1, ExpressaoInfo exp2, TabelaDeSimbolo.Tokens tokOpr) {
        if(exp1.tipo != exp2.tipo) {
            handleError(Error.ErrorTypes.TIPOS_INCOMPATIVEIS_SEMANTICO, null); // [2] validar alexei
        } else {
            if(exp1.tamanho > 0 || exp2.tamanho > 0) {
                if(exp1.tamanho > 0 && exp2.tamanho > 0 && exp1.tipo == Simbolo.Tipos.CHAR && (tokOpr == IGUAL)) { // [1] validar alexei
                    exp1.tipo = Simbolo.Tipos.BOOL;
                } else {
                    handleError(Error.ErrorTypes.TIPOS_INCOMPATIVEIS_SEMANTICO, null); // vetores + string(opk - ==)
                }
            } else {
                if(exp1.isCharacter()) {  // CHAR x CHAR
                    if(contemTokenOp(opAritmeticasInteiras, tokOpr)) {
                        handleError(Error.ErrorTypes.TIPOS_INCOMPATIVEIS_SEMANTICO, null);
                    } else {
                        if(contemTokenOp(opAritmeticasLogicas, tokOpr)) {
                            exp1.tipo = Simbolo.Tipos.BOOL;
                        } else { // SOMA de 2 characteres
                            exp2.tamanho = 2;
                        }
                    }
                } else if(exp1.isLogico()) { // BOOL x BOOL
                    handleError(Error.ErrorTypes.TIPOS_INCOMPATIVEIS_SEMANTICO, null); // bool
                } else if(exp1.isInteiro()){ // INT X INT
                    if(contemTokenOp(opAritmeticasLogicas, tokOpr)) {
                        exp1.tipo = Simbolo.Tipos.BOOL;
                    }
                } else {
                    handleError(Error.ErrorTypes.TIPOS_INCOMPATIVEIS_SEMANTICO, null);
                }
            }
        }
    }

    public void operacaoLogica(ExpressaoInfo exp1, ExpressaoInfo exp2, TabelaDeSimbolo.Tokens tokOpr) {
        if(!exp1.isLogico() || !exp2.isLogico()) {
            handleError(Error.ErrorTypes.TIPOS_INCOMPATIVEIS_SEMANTICO, null);
        } else {

        }
    }

    private boolean contemTokenAtual(TabelaDeSimbolo.Tokens[] tokens) {
        for(TabelaDeSimbolo.Tokens token: tokens)
            if(tokenAtual == token) {
                return true;
            }

        return false;
    }

    private boolean contemTokenOp(TabelaDeSimbolo.Tokens[] tokens, TabelaDeSimbolo.Tokens tokOpr) {
        for(TabelaDeSimbolo.Tokens token: tokens)
            if(tokOpr == token) {
                return true;
            }

        return false;
    }

    @Override
    public void handleError(Error.ErrorTypes type, String lexema) {
        System.out.print(anLexico.currentLinePos + ":");
        switch(type) {
            case TOKEN_NAO_ESPERADO_SINTATICO:
                System.out.println("token nao esperado [" + lexema + "].");
                break;
            case FIM_DE_ARQUIVO_NAO_ESPERADO:
                System.out.println("fim de arquivo não esperado.");
                break;
            case ID_NAO_DECLARADO_SEMANTICO:
                System.out.println("identificador nao declarado [" + lexema + "].");
                break;
            case ID_JA_DECLARADO_SEMANTICO:
                System.out.println("identificador ja declarado [" + lexema + "].");
                break;
            case CLASSE_DE_ID_INCOMPATIVEL_SEMANTICO:
                System.out.println("classe de identificador incompatível [" + lexema + "].");
                break;
            case TIPOS_INCOMPATIVEIS_SEMANTICO:
                System.out.println("tipos incompatíveis");
                break;
            case TAMANHO_MAXIMO_EXCEDIDO_SEMANTICO:
                System.out.println("tamanho do vetor excede o máximo permitido.");
                break;
        }

//        if(type != Error.ErrorTypes.ID_JA_DECLARADO_SEMANTICO && type != Error.ErrorTypes.ID_NAO_DECLARADO_SEMANTICO && type != Error.ErrorTypes.TIPOS_INCOMPATIVEIS_SEMANTICO) {
            System.exit(1);
//        }
    }
}

