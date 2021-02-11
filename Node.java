public class Node {
  private String name;     // Nome da classe
  private Node childs[];   // O numero de filhos é dado pela quantidade de valores associados a um atributo
  private int count;       // Numero de ocorrencias da classe OU pos do atributo
  private String[] branch; // Nome do ramo[i] (atributo)

  // Contrutor usado para representar uma folha
  Node(String name, int count) {
    this.count = count;
    this.name = name;
  }
  // Construtor de nó que se encontra a meio da arvore
  Node(Atribute A) { 
    name = A.getName();
    childs = new Node[A.getValues().size()];
    branch = A.getValues().toArray(new String[childs.length]);
    count = A.getPosition();
  }

  // Liga ao nó corrente um novo nó no mesmo indice que value no atributo
  public void expand(String value, Atribute A, Node sub_tree) {
    childs[A.getValues().indexOf(value)] = sub_tree;
  }

  // Se o nó não tiver filhos retorna TRUE senão FALSE
  public boolean isLeaf() {
    return childs == null;
  }

  // Obter os nós sucessores
  public Node getSucessor(String[] example) {
    for(int i=0; i<branch.length; i++) {
      if(branch[i].equals(example[count])) {
        return childs[i];
      }
    }

    return null;
  }

  // Representacao do nó em string
  public String toString() {
    if(childs == null) {
      return name + " (" + count + ")";  // class1 (counter1)
    }
    else {
      return "<" + name + ">";           // <attribute2>
    }
  }

  //Getters and Setters
  public Node[] getChilds() {
    return childs;
  }
  public String[] getBranch() {
    return branch;
  }
  public String getName() {
    return name;
  }
  public int getCount() {
    return count;
  }
  public void addCount(int n) {
    count += n;
  }
}