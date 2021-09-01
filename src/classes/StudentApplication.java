package classes;

import annotatons.Component;
import annotatons.Autowired;
import annotatons.Qualifier;
import interfaces.StudentApplicationinterface;
import interfaces.StudentRegisterServiceInterface;

@Component
public
class StudentApplication implements StudentApplicationinterface {

    //@Autowired
    //@Qualifier("studentRegisterService")
    public StudentApplication(StudentRegisterServiceInterface studentRegisterService) {
        this.studentRegisterService = studentRegisterService;
    }

    public StudentApplication() {
    }

    //@Autowired
    //@Qualifier("studentRegisterService")
    public void setStudentRegisterService(StudentRegisterServiceInterface studentRegisterService) {
        this.studentRegisterService = studentRegisterService;
    }

    private String name;              // to identify the object

    public void setName(String name) {
        this.name = name;
    }


    public String getName() {
        return name;
    }

    @Autowired
    @Qualifier("studentRegisterService")
    StudentRegisterServiceInterface studentRegisterService;

    @Override

    public String toString() {
        return "StudentApplication{\n" +
                "studentRegisterService=" + studentRegisterService +
                "}\n";
    }
}
