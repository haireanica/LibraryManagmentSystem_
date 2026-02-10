public class Patron {
    //Attributes
    private  String PatronID;
    private  String Name;
    private  String Address;
    private  double FineAmount;

    //Constructor
    public Patron(String PatronID,String Name, String Address, double FineAmount){
        //verify if fine amount is in range before creating object
        if (FineAmount < 0 || FineAmount > 250) {
            throw new IllegalArgumentException("Overdue fine must be between $0 and $250.");
        }
        this.PatronID = PatronID;
        this.Name = Name;
        this.Address = Address;
        this.FineAmount = FineAmount;
    }

    //Get and Set methods
    public String getPatronID() {
        return PatronID;
    }
    public void setPatronID(String PatronID) {
        this.PatronID = PatronID;
    }
    public String getName() {
        return Name;
    }
    public void setName(String Name) {
        this.Name = Name;
    }
    public String getAddress(){
        return Address;
    }
    public void setAddress(String Address){
        this.Address = Address;
    }
    public double getFineAmount(){
        return FineAmount;
    }
    public void setFineAmount(double FineAmount){
        this.FineAmount = FineAmount;
    }

    //toString method to print details of patron(s)
    @Override
    public String toString(){
        return "ID: " + PatronID + " | Name: " + Name + " | Address: " + Address + " | FineAmount: " + String.format("%.2f", FineAmount);
    }
}
