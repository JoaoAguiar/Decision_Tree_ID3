import java.util.*;

public class Atribute{
  private String name;          // Nome do atributo
  private List<String> values;  // Lista com os valores possiveis
  private int position;         // Posicao no array de testes

  Atribute(List<String[]> examples, int position) {
    this.position = position;                                    // Atualiza posicao
    this.name = examples.get(0)[position];                       // Atualiza nome

    values = new LinkedList<String>();
    ListIterator<String[]> iterator = examples.listIterator(1);  // 0 estão nomes dos atributos

    while(iterator.hasNext()) {                                    
      String[] temp = iterator.next();                           // Recebe linha dos testes
      
      if(!values.contains(temp[position])) {
        values.add(temp[position]);                           
      }
    }

    Collections.sort(values);
  }

  // Getters and Setters
  public List<String> getValues(){
    return values;
  }
  public int getPosition(){
    return position;
  }
  public String getName(){
    return name;
  }

  // Representacao do atributo em string NAME [valor1, valor2, valor3, ...]
  public String toString(){
    return name + " " + values.toString();
  }
}