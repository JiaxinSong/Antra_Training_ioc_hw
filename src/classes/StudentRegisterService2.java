package classes;

import annotatons.Component;
import interfaces.StudentRegisterServiceInterface;
@Component
public class StudentRegisterService2 implements StudentRegisterServiceInterface {
    private String name;             // to identify the object

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "this is student register service instance : " + super.toString() + "\n";
    }
}
