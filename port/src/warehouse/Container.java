package warehouse;
/* Старая версия:
public class Container {
    private int id;
	
    public Container(int id){
	this.id = id;
    }
	
    public int getId(){
	return id;
    }
}
Container-бин, который мы засовываем в коллекции. Поэтому нужно переопределить equals,hashCode,toString, имплементировать Serializable
и объявить дефолтный конструктор
*/
public class Container implements Serializable{
    private static final long serialVersionUID = 1L;
    private int id;

    public Container() {
    }
	    
    public Container(int id){
	this.id = id;
    }

    public int getId() {
        return id;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 53 * hash + this.id;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Container other = (Container) obj;
        if (this.id != other.id) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Container{" + "id=" + id + '}';
    }
    
}
