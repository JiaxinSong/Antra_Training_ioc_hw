import annotatons.Component;
import annotatons.Qualifier;
import classes.StudentApplication;
import classes.StudentRegisterService;
import classes.StudentRegisterService2;
import exceptions.MultipleImplementException;
import annotatons.Autowired;
import interfaces.StarterInterface;
import interfaces.StudentApplicationinterface;
import interfaces.StudentRegisterServiceInterface;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;


public class Container {
    private final Map<String, Object> objectFactory = new HashMap<>();

    public static void start() throws Exception{
        Container c = new Container();
        List<Class<?>> classes = c.scan();
        c.register(classes);
        c.injectObjects(classes);
    }

    private List<Class<?>> scan() {
        return Arrays.asList(StudentRegisterService.class, StudentApplication.class, Starter.class);
    }

    private boolean register(List<Class<?>> classes) throws Exception {
        for(Class<?> clazz: classes) {
            Annotation[] annotations = clazz.getAnnotations();
            for(Annotation a: annotations) {
                if(a.annotationType() == Component.class) {
                    objectFactory.put(clazz.getName(), clazz.getDeclaredConstructor(null).newInstance());
                }
            }

            //if constructor has autowired  just remove object
            Constructor[] constructors=clazz.getDeclaredConstructors();
            for(Constructor c:constructors){
                Annotation[] annotations2 = c.getAnnotations();
                for(Annotation aa:annotations2){
                    if(aa.annotationType() == Autowired.class) {
                        objectFactory.remove(clazz.getName());
                    }
                }

            }


        }
       // System.out.println(objectFactory);
        StudentRegisterService st=new StudentRegisterService();
        st.setName("studentRegisterService");
        objectFactory.put(st.getClass().getName(),st);
        return true;
    }

    private boolean injectObjects(List<Class<?>> classes) throws Exception{
        for(Class<?> clazz: classes) {

            //check constructor injection
            Constructor[] constructors=clazz.getDeclaredConstructors();
            for(Constructor c:constructors){
                Annotation[] annotations = c.getAnnotations();
                Boolean isAutowired=false;         //check the existence of annotatons.Autowired
                Boolean isQualifier=false;         //check the existence of qualifier
                String qualname=null;   //store the value of qualifier

                for(Annotation a: annotations) {
                    if(a.annotationType() == Autowired.class) {
                        isAutowired=true;
                    }
                    if(a.annotationType()== Qualifier.class){
                        isQualifier=true;
                        Qualifier q= (Qualifier) c.getAnnotation(Qualifier.class);
                        qualname=q.value();

                    }
                }
                if(isAutowired && (!isQualifier)) {             //only have autowired  should check whether there are different impl

                    Class[] paramTypes = c.getParameterTypes();
                    if(paramTypes.length==1) {
                        Object injectInstance = null;
                        int countclass = 0;                             //count the number of classes implements the interface
                        for (Map.Entry<String, Object> entry : objectFactory.entrySet()) {
                            String s = entry.getKey();
                            if (paramTypes[0].isAssignableFrom(Class.forName(s))) {
                                countclass++;
                                injectInstance = entry.getValue();
                            }
                        }
                        if (countclass > 1) {                       //there are different implthrow new MultipleImplementException("Have multiple implementations!");
                            throw new MultipleImplementException("Have multiple implementations.");
                        }

                        c.setAccessible(true);
                        objectFactory.put(clazz.getName(),c.newInstance(injectInstance));

                    }
                }else if(isAutowired&&isQualifier){
                    Object injectInstance = null;
                    for (Map.Entry<String, Object> entry : objectFactory.entrySet()) {
                        Object x = entry.getValue();
                        if(x instanceof StudentApplication){
                            StudentApplication s=(StudentApplication) x;
                            if(Objects.equals(s.getName(), qualname)){
                                injectInstance=x;
                            }
                        }
                        if(x instanceof StudentRegisterService){
                            StudentRegisterService s=(StudentRegisterService) x;
                            if(Objects.equals(s.getName(), qualname)){
                                injectInstance=x;
                            }
                        }
                        if(x instanceof Starter){
                            Starter s=(Starter) x;
                            if(Objects.equals(s.getName(), qualname)){
                                injectInstance=x;
                            }
                        }
                    }
                    c.setAccessible(true);
                    objectFactory.put(clazz.getName(),c.newInstance(injectInstance));

                }


            }



            //check field injection
            Field[] fields = clazz.getDeclaredFields();
            Object curInstance = objectFactory.get(clazz.getName());
            for(Field f: fields) {
                Annotation[] annotations = f.getAnnotations();
                Boolean isAutowired=false;         //check the existence of annotatons.Autowired
                Boolean isQualifier=false;         //check the existence of qualifier
                String qualname=null;   //store the value of qualifier

                for(Annotation a: annotations) {
                    if(a.annotationType() == Autowired.class) {
                        isAutowired=true;
                    }
                    if(a.annotationType()== Qualifier.class){
                        isQualifier=true;
                        Qualifier q=f.getAnnotation(Qualifier.class);
                        qualname=q.value();

                    }
                }

                if(isAutowired && (!isQualifier)) {             //only have autowired  should check whether there are different impl
                    Class<?> type = f.getType();
                    Object injectInstance = null;
                    int countclass=0;                             //count the number of classes implements the interface
                    for (Map.Entry<String, Object> entry : objectFactory.entrySet()) {
                        String s = entry.getKey();
                        if (type.isAssignableFrom(Class.forName(s))) {
                            countclass++;
                            injectInstance = entry.getValue();
                        }
                    }
                    if(countclass>1){                       //there are different implthrow new MultipleImplementException("Have multiple implementations!");
                        throw new MultipleImplementException("Have multiple implementations.");
                    }
                    f.setAccessible(true);
                    f.set(curInstance, injectInstance);
                }else if(isAutowired && isQualifier){              //have qualifier
                    Class<?> type = f.getType();
                    Object injectInstance = null;
                    for (Map.Entry<String, Object> entry : objectFactory.entrySet()) {
                        Object x = entry.getValue();
                        if(x instanceof StudentApplication){
                            StudentApplication s=(StudentApplication) x;
                            if(Objects.equals(s.getName(), qualname)){
                                injectInstance=x;
                            }
                        }
                        if(x instanceof StudentRegisterService){
                            StudentRegisterService s=(StudentRegisterService) x;
                            if(Objects.equals(s.getName(), qualname)){
                                injectInstance=x;
                            }
                        }
                        if(x instanceof Starter){
                            Starter s=(Starter) x;
                            if(Objects.equals(s.getName(), qualname)){
                                injectInstance=x;
                            }
                        }
                    }
                    f.setAccessible(true);
                    f.set(curInstance, injectInstance);
                }
            }
            //check method injection
            Method[] methods = clazz.getDeclaredMethods();
            for(Method m:methods){
                Annotation[] annotations = m.getAnnotations();
                Boolean isAutowired=false;         //check the existence of annotatons.Autowired
                Boolean isQualifier=false;         //check the existence of qualifier
                String qualname=null;   //store the value of qualifier

                for(Annotation a: annotations) {
                    if(a.annotationType() == Autowired.class) {
                        isAutowired=true;
                    }
                    if(a.annotationType()== Qualifier.class){
                        isQualifier=true;
                        Qualifier q=m.getAnnotation(Qualifier.class);
                        qualname=q.value();

                    }
                }

                if(isAutowired && (!isQualifier)) {             //only have autowired  should check whether there are different impl

                    Class[] paramTypes = m.getParameterTypes();
                    if(paramTypes.length==1) {
                        Object injectInstance = null;
                        int countclass = 0;                             //count the number of classes implements the interface
                        for (Map.Entry<String, Object> entry : objectFactory.entrySet()) {
                            String s = entry.getKey();
                            if (paramTypes[0].isAssignableFrom(Class.forName(s))) {
                                countclass++;
                                injectInstance = entry.getValue();
                            }
                        }
                        if (countclass > 1) {                       //there are different implthrow new MultipleImplementException("Have multiple implementations!");
                            throw new MultipleImplementException("Have multiple implementations.");
                        }
                        m.setAccessible(true);
                        m.invoke(curInstance,injectInstance);

                    }else if(paramTypes.length==2){
                        Object injectInstance1 = null;
                        Object injectInstance2 = null;

                        int countclass1 = 0;
                        int countclass2=0;  //count the number of classes implements the interface
                        for (Map.Entry<String, Object> entry : objectFactory.entrySet()) {
                            String s = entry.getKey();
                            if (paramTypes[0].isAssignableFrom(Class.forName(s))) {
                                countclass1++;
                                injectInstance1 = entry.getValue();
                            }
                            if (paramTypes[1].isAssignableFrom(Class.forName(s))) {
                                countclass2++;
                                injectInstance2 = entry.getValue();
                            }
                        }
                        if (countclass1 > 1|| countclass2>1) {                       //there are different implthrow new MultipleImplementException("Have multiple implementations!");
                            throw new MultipleImplementException("Have multiple implementations.");
                        }
                        m.setAccessible(true);
                        m.invoke(curInstance,injectInstance1,injectInstance2);

                    }
                }else if(isAutowired&&isQualifier){
                    Object injectInstance = null;
                    for (Map.Entry<String, Object> entry : objectFactory.entrySet()) {
                        Object x = entry.getValue();
                        if(x instanceof StudentApplication){
                            StudentApplication s=(StudentApplication) x;
                            if(Objects.equals(s.getName(), qualname)){
                                injectInstance=x;
                            }
                        }
                        if(x instanceof StudentRegisterService){
                            StudentRegisterService s=(StudentRegisterService) x;
                            if(Objects.equals(s.getName(), qualname)){
                                injectInstance=x;
                            }
                        }
                        if(x instanceof Starter){
                            Starter s=(Starter) x;
                            if(Objects.equals(s.getName(), qualname)){
                                injectInstance=x;
                            }
                        }
                    }
                    m.setAccessible(true);
                    m.invoke(curInstance,injectInstance);

                }

            }

        }
        return true;
    }
}


@Component
class Starter implements StarterInterface {
    @Autowired
    private static StudentApplicationinterface studentApplication;
    @Qualifier("studentRegisterService")
    @Autowired
    private static StudentRegisterServiceInterface studentRegisterService;

    //@Autowired
    public static void setStudentApplication(StudentApplicationinterface studentApplication) {
        Starter.studentApplication = studentApplication;
    }
    //@Autowired
    //@Qualifier("studentRegisterService")
    public static void setStudentRegisterService(StudentRegisterServiceInterface studentRegisterService) {
        Starter.studentRegisterService = studentRegisterService;
    }

    private String name;                // to identify the object
    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }

    public static void main(String[] args) throws Exception{
//        Class<?> clazz=StudentApplication.class;
//        Method method[] = clazz.getDeclaredMethods();
//        Constructor[] c=clazz.getDeclaredConstructors();
//        for(int i=0;i<c.length;i++){
//            System.out.println(c[i]);
//
//        }
//        for(int i=0;i<method.length;i++){
//            System.out.println(method[i]);
//            System.out.println(method[i].getName());
//        }
        Container.start();
        System.out.println(studentApplication);
        System.out.println(studentRegisterService);



    }
}