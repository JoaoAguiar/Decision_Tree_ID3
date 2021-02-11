import java.io.*;
import java.util.*;

public class DecisionTree {
  public static Atribute atribute_class;  // Atribute geral representando a class

  public static void main(String[] args) {
    Scanner input = new Scanner(System.in);

    System.out.println("Welcome, try the decision tree model");

    Node root = null;

    while (true) {
      System.out.println("\n\n1) Build decision tree\n");
      System.out.println("2) Print decision tree\n");
      System.out.println("3) Test decision tree\n");
      System.out.println("4) Exit\n\n");
      System.out.print(">>>");
      int choice = input.nextInt();

      switch(choice) {
        case 1:
          clear_screen();
          List<String[]> examplesBuild = read_tree(input);          // Lê o ficheiro e converte para lista (cada linha é do tipo [])
          List<Atribute> atributes = get_atributes(examplesBuild);  // Cria uma lista de atributos e respetivos valores associados, para cada entrada de atributo
          examplesBuild.remove(0);                                  // Remove a primerira linha (nomes dos atributos)
          root = decision_tree(examplesBuild, atributes, null);     // Retorna ROOT da Arvore de Decisao
          clear_screen();

          break;
        case 2:
          clear_screen();

          if (root != null) {
            print_tree(root, "\t");
          } 
          else {
            System.out.println("\nYou need to build a tree first!");
          }

          break;
        case 3:
          clear_screen();

          if (root != null) {
            List<String[]> examplesTest = read_tree(input);
            classify(examplesTest, root);
          } 
          else {
            System.out.println("\nYou need to build a tree first!");
          }

          break;
        case 4:
          System.out.println("\nHasta la vista!\n\n");

          return;
        default:
          System.out.println("\nTry again ...");

          break;
      }
    }
  }

  // Função para limpar ecra
  public static void clear_screen() {
    System.out.print("\033[H\033[2J");
    System.out.flush();
  }

  // Função para ler input, retorna ficherio CSV e retornar uma lista de Strings que são os atributos
  public static List<String[]> read_tree(Scanner input) {
    List<String[]> examples = new LinkedList<String[]>();

    try {
      System.out.println("CSV file path:");

      String aux = input.next();
      File file = new File(aux);
      
      while(!file.exists()) {
        clear_screen();

        System.out.print("\n\nWrong path, try again");

        aux = input.next();
        file = new File(aux);
      }
      
      BufferedReader buffer = new BufferedReader(new FileReader(aux));
      String line = "";
      
      while((line = buffer.readLine()) != null) {
        String[] atributes = line.split(",");
        examples.add(atributes);
      }

      buffer.close();
    } 
    catch (FileNotFoundException e) {
      e.printStackTrace();
    } 
    catch (IOException e) {
      e.printStackTrace();
    } 

    return examples;
  }  
  
  public static List<Atribute> get_atributes(List<String[]> examples) {
    List<Atribute> atributes = new LinkedList<Atribute>();

    for(int i=1; i<examples.get(0).length-1; i++) {                     // 0 são os ID's
      atributes.add(new Atribute(examples, i));                         // Cria atributo
    }

    atribute_class = new Atribute(examples, examples.get(0).length-1);  // Cria o representante da class que se situa na ultima posiçao

    return atributes;
  }

  // Funcao principal na formacao da arvore
  public static Node decision_tree(List<String[]> examples, List<Atribute> atributes, List<String[]> parent_examples) {
    if(examples.isEmpty()) {                                             // Caso ja nao tenha mais exemplos para avaliar
      return plurality_value(parent_examples, false);                    
    }
    else if(same_class(examples)) {                                      // Se todos os exemplos chegam a mesma conclusao (tem a mesma classe)
      return new Node(examples.get(0)[atribute_class.getPosition()], examples.size());
    }
    else if(atributes.isEmpty()) {                                       // Caso nao possuia mais atributos
      return plurality_value(examples, true);                                  
    }
    else {
      Atribute A = importance(examples, atributes);                      // Recebe o atributo mais relevante na distincao
      Node tree = new Node(A);                                           // Cria um nó com base no atributo mais relevante
      List<Atribute> not_A = remove_importante_atribute(atributes, A);
      
      // Para todos os valores que o atributo possui
      for(String value:A.getValues()) {                               
        List<String[]> examples_value = getExamplesValues(A.getPosition(), value, examples);       
        Node sub_tree = decision_tree(examples_value, not_A, examples);  // Chamada recursiva
        tree.expand(value, A, sub_tree);                                 // Adiciona a chamada recursiva ao no corrente no ramo do valor vk
      }

      return tree;
    }
  }

  // Retorna o valor que ocorre com mais frequencia nos testes
  // True caso ainda existam testes, e False no caso contrario
  private static Node plurality_value(List<String[]> parent_examples, boolean people) { 
    int frequency[] = new int[atribute_class.getValues().size()];
    int max = 0;
    int index_max = 0;

    for(String[] example:parent_examples) {
      ListIterator<String> iterator = atribute_class.getValues().listIterator();
      int i = 0;
      
      while(iterator.hasNext()) { 
        String temp = iterator.next();

        if(example[atribute_class.getPosition()].equals(temp)) {
          frequency[i]++;
        }

        i++;
      }
    }

    for(int i=0; i<frequency.length; i++) {
      if(frequency[i] > max) {
        max = frequency[i];
        index_max = i;
      }
    }

    if(people) {
      return new Node(atribute_class.getValues().get(index_max), parent_examples.size());
    }
    else {
      return new Node(atribute_class.getValues().get(index_max), 0);
    }
  }

  // Retorna TRUE se todos os exemplos na lista levarem a mesma classe
  private static boolean same_class(List<String[]> examples) {
    String aux = examples.get(0)[atribute_class.getPosition()];
    
    for(String[] temp:examples) {
      if(!aux.equals(temp[atribute_class.getPosition()])) {
        return false;
      }
    }
    
    return true;
  }

  // Calculo do atributo com mais relevancia
  public static Atribute importance(List<String[]> examples, List<Atribute> atributes) { 
    double min = 1;                     // Qualquer valor maior que 1 porque a entropia no maximo atinge 1
    Atribute atribute_relevant = null;  // Atributo mais relevante

    for(Atribute atribute_temp:atributes) {
      double temp1 = 0;
      
      for(String temp2:atribute_temp.getValues()) {
        int array[] = calc(temp2, atribute_temp.getPosition(), examples);
        int aux = 0; 

        for(int i=0; i<array.length; i++) {
          aux += array[i];
        }

        temp1 += (aux/examples.size()) * entropy(array);
      }
      
      if(temp1 < min) {
        min = temp1;
        atribute_relevant = atribute_temp;
      } 
      else if(temp1 == min) {
        // Caso o valor seja igual escolhe o atributo com menos valores
        if(atribute_temp.getValues().size() < atribute_relevant.getValues().size()) { 
          atribute_relevant = atribute_temp;
        }
      }
    }

    return atribute_relevant;
  }

  private static int[] calc(String value, int position, List<String[]> examples) {
    int[] array = new int[atribute_class.getValues().size()];

    for(String[] temp:examples) {
      if(temp[position].equals(value)) {
        for(int i=0; i<atribute_class.getValues().size(); i++) {
          if(temp[atribute_class.getPosition()].equals(atribute_class.getValues().get(i))) {
            array[i]++;
          }
        }
      }
    }

    return array;
  }

  private static double entropy(int array[]) {
    double entropy = 0;
    double aux = 0;

    for(int i=0; i<array.length; i++) {
      if(array[i] != 0) {
        for(int j=0; j<array.length; j++) {
          aux += array[j];
        }

        double temp;

        if(aux != 0) {
          temp = (double) array[i] / aux;
        }
        else {
          temp = 0;          
        }

        entropy -= temp * (Math.log(temp)/Math.log(2));
      }
    }

    return entropy;
  }

  public static List<Atribute> remove_importante_atribute(List<Atribute> atributes, Atribute A) {
    List<Atribute> list = new LinkedList<Atribute>();

    for(Atribute temp:atributes) {
      if(temp != A) {
        list.add(temp);
      }
    }

    return list;
  }

  // Lista com todos os exemplos com o valor de value no atributo A
  private static List<String[]> getExamplesValues(int position, String value, List<String[]> examples) {
    List<String[]> list = new LinkedList<String[]>();

    for (String[] temp:examples) {
      if (temp[position].equals(value)) {
        list.add(temp);
      }
    }

    return list;
  }

  // Imprime a arvore
  public static void print_tree(Node root, String space) {
    System.out.println(root);

    if(root.isLeaf()) {
      return;
    }

    Node[] childs = root.getChilds();

    for(int i=0; i<childs.length; i++) {
      System.out.print(space + root.getBranch()[i]+ ": ");

      print_tree(childs[i], space + "\t");
    }
  }

  private static void classify(List<String[]> examples, Node root) {
    System.out.println("\n\n");

    String[] classification = new String[examples.size()];
    int i = 0;

    for(String[] current:examples) {
      Node temp = root;

      while(!temp.isLeaf()){
        temp = temp.getSucessor(current);
      }

      classification[i++] = temp.getName();
    }

    for (i=0; i<classification.length; i++) {
      System.out.println("The sample " + (i+1) + " got: " + classification[i]);
    }

    System.out.println("\n");
  }
}