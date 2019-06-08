/* deve funcionar */
Var integer n[3], max;

  For I=0to 2do {
    write("Digite um numero: ");
    readln(n);
  }
  If n[0] > n[1] then {
    If n[0] > n[2] then max=n[0]; else max=n[2];
  } else If n[1] > n[2] then max=n[1]; else max=n[2]; 
  writeln("O maior numero e ",max);
