package LC;

public class Simbolo {
   byte tByte;
   String lexema;
   Classes classe;
   Tipos tipo;
   int tamanho;
   String valor;

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

   public boolean isInteiro() {
      return ( tipo == Simbolo.Tipos.INT  && tamanho == 0);
   }

   public boolean isArrayDeInteiro() {
      return ( tipo == Simbolo.Tipos.INT  && tamanho > 0);
   }

   public boolean isCharacter() {
      return ( tipo == Simbolo.Tipos.CHAR && tamanho == 0);
   }

   public boolean isArrayDeCharacter() {
      return ( tipo == Simbolo.Tipos.CHAR && tamanho > 0);
   }

   public boolean isLogico() {
      return ( tipo == Simbolo.Tipos.BOOL && tamanho == 0);
   }
}