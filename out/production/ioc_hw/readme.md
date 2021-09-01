1. add interface   
Add StudentApplicationinterface, StudentRegisterServiceInterface and starterInterface 
2. components implement the interfaces 
3. @Autowired without @Qualifier  
Add an exception called MultipleImplementException. It will be thrown when we have multiple implementations of current type  
If you want to test this exception, please add StudentRegiserService2.class in the list in scan() method.  
When testing field with @Autowired, please comment all the @Qualifier and all the annotations of constructors and setters in StudentApplication.  
When testing constructor with @Autowired, please comment all the @Qualifier and all the annotations of fields and setters in StudentApplication.  
When testing setters with @Autowired, please comment all the @Qualifier and all the annotations of constructors and fields in StudentApplication.  
For annotations in Starter, you can choose to use the field or setter autowired. Please comment the other one when use another. 
4. @Autowired with @Qualifier  
I add a field called name in all components in order to match the value in @Qualifier.  
Please uncomment the codes in register
When use @Qualifier, we should uncomment @Autowired.
For fields, setters, constructors, to test one of them, you should comment the other two.  
  
Unsolved problems:
1. By implement @Autowired on constructors,classes in list 