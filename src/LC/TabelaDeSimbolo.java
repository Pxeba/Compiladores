package LC;

import com.sun.xml.internal.bind.v2.model.core.ID;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

class TabelaDeSimbolo {

   Map<Byte,Simbolo> tabela;
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
         if(value > 37) {
            return Tokens.ID;
         } else {
            return TokensMap.get(value);
         }
      }

      Tokens(int tByte) {
         this.tByte = (byte) tByte;
      }
   }

   static TabelaDeSimbolo instance = new TabelaDeSimbolo();
   
   private TabelaDeSimbolo() {
      tabela = new HashMap<Byte,Simbolo>();
   }
   
   void setup() {
      tabela.put(Tokens.CHAR.tByte, new Simbolo(Tokens.CHAR.tByte,Tokens.CHAR.name()));
      tabela.put(Tokens.INTEGER.tByte, new Simbolo(Tokens.INTEGER.tByte,Tokens.INTEGER.name()));
      tabela.put(Tokens.CONST.tByte, new Simbolo(Tokens.CONST.tByte,Tokens.CONST.name()));
      tabela.put(Tokens.ELSE.tByte, new Simbolo(Tokens.ELSE.tByte,Tokens.ELSE.name()));
      tabela.put(Tokens.PARENTESES_ABERTO.tByte, new Simbolo(Tokens.PARENTESES_ABERTO.tByte,"("));
      tabela.put(Tokens.MENOR_IGUAL.tByte, new Simbolo(Tokens.MENOR_IGUAL.tByte,"<="));
      tabela.put(Tokens.PONTO_E_VIRGULA.tByte, new Simbolo(Tokens.PONTO_E_VIRGULA.tByte,";"));
      tabela.put(Tokens.WRITE.tByte, new Simbolo(Tokens.WRITE.tByte,Tokens.WRITE.name()));
      tabela.put(Tokens.VAR.tByte, new Simbolo(Tokens.VAR.tByte,Tokens.VAR.name()));
      tabela.put(Tokens.AND.tByte, new Simbolo(Tokens.AND.tByte,Tokens.AND.name()));
      tabela.put(Tokens.PARENTESES_FECHADO.tByte, new Simbolo(Tokens.PARENTESES_FECHADO.tByte,")"));
      tabela.put(Tokens.VIRGULA.tByte, new Simbolo(Tokens.VIRGULA.tByte,","));
      tabela.put(Tokens.ABRE_CHAVE.tByte, new Simbolo(Tokens.ABRE_CHAVE.tByte,"{"));
      tabela.put(Tokens.WRITELN.tByte, new Simbolo(Tokens.WRITELN.tByte,Tokens.WRITELN.name()));
      tabela.put(Tokens.OR.tByte, new Simbolo(Tokens.OR.tByte,Tokens.OR.name()));
      tabela.put(Tokens.MENOR_QUE.tByte, new Simbolo(Tokens.MENOR_QUE.tByte,"<"));
      tabela.put(Tokens.SOMA.tByte, new Simbolo(Tokens.SOMA.tByte,"+"));
      tabela.put(Tokens.FECHA_CHAVE.tByte, new Simbolo(Tokens.FECHA_CHAVE.tByte,"}"));
      tabela.put(Tokens.PORCENTAGEM.tByte, new Simbolo(Tokens.PORCENTAGEM.tByte,"%"));
      tabela.put(Tokens.NOT.tByte, new Simbolo(Tokens.NOT.tByte,Tokens.NOT.name()));
      tabela.put(Tokens.MAIOR_QUE.tByte, new Simbolo(Tokens.MAIOR_QUE.tByte,">"));
      tabela.put(Tokens.MENOS.tByte, new Simbolo(Tokens.MENOS.tByte,"-"));
      tabela.put(Tokens.THEN.tByte, new Simbolo(Tokens.THEN.tByte,Tokens.THEN.name()));
      tabela.put(Tokens.ABRE_COLCHETE.tByte, new Simbolo(Tokens.ABRE_COLCHETE.tByte,"["));
      tabela.put(Tokens.FOR.tByte, new Simbolo(Tokens.FOR.tByte,Tokens.FOR.name()));
      tabela.put(Tokens.IGUAL.tByte, new Simbolo(Tokens.IGUAL.tByte,"="));
      tabela.put(Tokens.DIFERENTE.tByte, new Simbolo(Tokens.DIFERENTE.tByte,"<>"));
      tabela.put(Tokens.MULTIPLICACAO.tByte, new Simbolo(Tokens.MULTIPLICACAO.tByte,"*"));
      tabela.put(Tokens.READLN.tByte, new Simbolo(Tokens.READLN.tByte,Tokens.READLN.name()));
      tabela.put(Tokens.FECHA_COLCHETE.tByte, new Simbolo(Tokens.FECHA_COLCHETE.tByte,"]"));
      tabela.put(Tokens.IF.tByte, new Simbolo(Tokens.IF.tByte,Tokens.IF.name()));
      tabela.put(Tokens.TO.tByte, new Simbolo(Tokens.TO.tByte,Tokens.TO.name()));
      tabela.put(Tokens.MAIOR_IGUAL.tByte, new Simbolo(Tokens.MAIOR_IGUAL.tByte,">="));
      tabela.put(Tokens.BARRA.tByte, new Simbolo(Tokens.BARRA.tByte,"/"));
      tabela.put(Tokens.STEP.tByte, new Simbolo(Tokens.STEP.tByte,Tokens.STEP.name()));
      tabela.put(Tokens.DO.tByte, new Simbolo(Tokens.DO.tByte,Tokens.DO.name()));
      tabela.put(Tokens.KVALUE.tByte, new Simbolo(Tokens.KVALUE.tByte,Tokens.KVALUE.name()));
   }

   Simbolo buscarToken(String lexema) {
      byte token = -1;
      Collection<Simbolo> simbolos = tabela.values();
      for( Simbolo s: simbolos ) {
         if (s.lexema.toLowerCase().equals(lexema)) {
            token = s.tByte;
            break;
         }
      }

      if(token == -1) { // nenhum token encontrado, logo identificador
          inserirIdentificador(lexema);
          return tabela.get((byte) (tabela.size()));
      }
     else {
          return tabela.get(token);
      }
   }

   private void inserirIdentificador(String id) {
      tabela.put((byte) (tabela.size() +1), new Simbolo((byte) (tabela.size() +1), id));
   }

//   public void testarTabela() {
//       for(int i=0;i<tabela.size();i++) {
//            System.out.println(tabela.get(i).lexema);
//       }
//   }
}