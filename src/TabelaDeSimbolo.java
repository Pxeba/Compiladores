package src;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class TabelaDeSimbolo { 

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