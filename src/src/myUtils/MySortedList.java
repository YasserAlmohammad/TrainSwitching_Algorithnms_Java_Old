package myUtils;

/**
 *
 * <p>Title: </p>
 *
 * <p>Description:
 * sorted linked list implementation, it is required that inserted elements implements Comparable interface
 * <br> if not a runtime exception will rise that is not caught in this class's code
 * <br>
 * </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class MySortedList{
    protected Node head;
     public MySortedList(){
         head=null;
     }

     /**
      * check if the list has any elements
      * @return boolean
      */
     public boolean isEmpty(){
         if(head==null)
             return true;
         return false;
     }

     /**
      * the inserted object must implement Comparable interface or a runtime exception ClassCastException will rise
      * @param obj Object
      * @return Object
      */
     public Object insert(Object obj){
         if(head==null){ //first element
             head = new Node(obj, null);
             return obj;
         }

         //check if it's less than the first element
         if((((Comparable)obj).compareTo(head.obj))<=0){
             Node newNode=new Node(obj,head);
             head=newNode;
             return obj;
         }

         Node temp=head;
         while(temp.next!=null){
             if((((Comparable)obj).compareTo(temp.next.obj))<=0){
                 Node newNode=new Node(obj,temp.next);
                 temp.next=newNode;
                 return obj;
             }
             temp=temp.next;
         }

         temp.next=new Node(obj,null); //final element
         return obj;
     }

     /**
      * not implemented yet
      * @param obj Object
      * @return Object
      */
     public Object remove(Object obj){
         return obj;
     }

     /**
      * put each element of the list in a single line
      * @return String
      */
     public String toString(){
         StringBuffer content=new StringBuffer();
         Node temp=head;
         while(temp!=null){
             content.append("\n");
             content.append(temp.obj.toString());
             temp=temp.next;
         }

         return content.toString();
     }
}
