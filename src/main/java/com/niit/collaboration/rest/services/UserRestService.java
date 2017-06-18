package com.niit.collaboration.rest.services;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.niit.collaboration.dao.FriendDAO;
import com.niit.collaboration.dao.UserDAO;
import com.niit.collaboration.model.Friend;
import com.niit.collaboration.model.User;



@CrossOrigin("http://localhost:8077")
@RestController
public class UserRestService 
{
	
	private Logger log = LoggerFactory.getLogger(UserRestService.class);
	
	
	
	@Autowired
	User user;
	
	@Autowired
	UserDAO userDAO;
	
	@Autowired
	Friend friend;
	
	@Autowired
	FriendDAO friendDAO;
	
	@Autowired
	private HttpSession session;
	
	
	
	//Simple Test whether restcontroller is working or not
	
	//http://localhost:8080/CollaborationRestSerivce/hello
	@GetMapping("/hello")
	public String sayHello()
	{
		return "  Hello from User rest service Modifed message";
	}
	
	@GetMapping("/")
	public String Hello()
	{
		return "  Server started";
	}
	
	
	
	//getAllUsers - @GetMapping    //ResPonsEntity
	
	//http://localhost:8080/CollborationRestService/users
	@GetMapping("/users")
	public ResponseEntity< List<User>> getAllPendingUsers()
	{
		List<User> userList =  userDAO.list();
		
		return   new ResponseEntity<List<User>>(userList, HttpStatus.OK);
	}
	
	
	
	
	//http://localhost:8080/CollaborationResetService/user/niit
	/*@GetMapping("/user/{id}")
	public ResponseEntity<User> getUserByID(@PathVariable("id") String id)
	{
		log.debug("**************Starting of the method getUserByID");
		log.info("***************Trying to get userdetails of the id " + id);
		user = userDAO.get(id);
		
		if(user==null)
		{
			user = new User();
			user.setErrorCode("404");
			user.setErrorMessage("User does not exist with the id :" + id);
		}
		else
		{
			user.setErrorCode("200");
			user.setErrorMessage("success");
		}
		
		log.info("**************** Name of teh user is " + user.getName());
		log.debug("**************Ending of the method getUserByID");
	  return	new ResponseEntity<User>(user , HttpStatus.OK);
	}
	*/
	/*@RequestMapping("/validate/{id}/{password}")
	public ResponseEntity<User> validation(@PathVariable("id") String id , @PathVariable("password") String password){
		
		 if( userDAO.isValidate(id, password))
		 {
		user.setErrorCode("200");
		user.setErrorMessage("Valid credentails");
			 
		 }
		 else
		 {
		user.setErrorCode("400");
		user.setErrorMessage("invalid Valid credentails");
			 
		 }
		
		return new ResponseEntity<User>(user , HttpStatus.OK);
		
	}
	*/
	@RequestMapping(value = "/listAllUsersNotFriends/{hu}", method = RequestMethod.GET)
	public ResponseEntity<List<User>> listAllUsersNotFriends(@PathVariable ("hu") String hu ,HttpSession session) {

		log.debug("->->->->calling method listAllUsers");
		
		/*String loggedInUserID = (String) session.getAttribute(id);*/
		String usrid = hu;
		 session.getAttribute(usrid);
		
		log.debug("You are loggin with the role : " +session.getAttribute(usrid));
		
		
		List<User> users = userDAO.notMyFriendList(usrid);

		// errorCode :200 :404
		// errorMessage :Success :Not found

		if (users.isEmpty()) {
			user.setErrorCode("404");
			user.setErrorMessage("No users are available");
			users.add(user);
		}

		return new ResponseEntity<List<User>>(users, HttpStatus.OK);
	}

	// http://localhost:8080/Collaboration/user/
	@RequestMapping(value = "/user/", method = RequestMethod.POST)
	public ResponseEntity<User> createUser(@RequestBody User user) {
		log.debug("->->->->calling method createUser");
		
		if (userDAO.get(user.getId()) == null) {
			log.debug("->->->->User is going to create with id:" + user.getId());
		
			user.setIsOnline("N");
			
			/*user.setStatus("N");*/
			
			  if (userDAO.saveOrupdate(user) ==true)
			  {
				  user.setErrorCode("200");
					user.setErrorMessage("Thank you  for registration. You have successfully registered as " + user.getRole());
			  }
			  else
			  {
				  user.setErrorCode("404");
					user.setErrorMessage("Could not complete the operatin please contact Admin");
		
				  
			  }
			
			return new ResponseEntity<User>(user, HttpStatus.OK);
		}
		log.debug("->->->->User already exist with id " + user.getId());
		user.setErrorCode("404");
		user.setErrorMessage("User already exist with id : " + user.getId());
		return new ResponseEntity<User>(user, HttpStatus.OK);
	}

	
	@RequestMapping(value = "/user/", method = RequestMethod.PUT)
	public ResponseEntity<User> updateUser(@RequestBody User user) {
		log.debug("->->->->calling method updateUser");
		if (userDAO.get(user.getId()) == null) {
			log.debug("->->->->User does not exist with id " + user.getId());
			user = new User(); // ?
			user.setErrorCode("404");
			user.setErrorMessage("User does not exist with id " + user.getId());
			return new ResponseEntity<User>(user, HttpStatus.OK);
		}

		userDAO.saveOrupdate(user);
		log.debug("->->->->User updated successfully");
		return new ResponseEntity<User>(user, HttpStatus.OK);
	}


	// http://localhost:8081/CollaborationBackEnd/user/abbas
	@RequestMapping(value = "/user/{id}", method = RequestMethod.GET)
	public ResponseEntity<User> getUser(@PathVariable("id") String id) {
		log.debug("->->calling method getUser");
		log.debug("->->id->->" + id);
		User user = userDAO.get(id);
		if (user == null) {
			log.debug("->->->-> User does not exist wiht id" + id);
			user = new User(); //To avoid NLP - NullPointerException
			user.setErrorCode("404");
			user.setErrorMessage("User does not exist");
			return new ResponseEntity<User>(user, HttpStatus.OK);
		}
		log.debug("->->->-> User exist wiht id" + id);
		log.debug(user.getName());
		return new ResponseEntity<User>(user, HttpStatus.OK);
	}

	@RequestMapping(value = "/accept/{id}", method = RequestMethod.GET)
	public ResponseEntity<User> accept(@PathVariable("id") String id) {
		log.debug("Starting of the method accept");

		user = updateStatus(id, "A", "");
		log.debug("Ending of the method accept");
		return new ResponseEntity<User>(user, HttpStatus.OK);

	}

	@RequestMapping(value = "/reject/{id}/{reason}", method = RequestMethod.GET)
	public ResponseEntity<User> reject(@PathVariable("id") String id, @PathVariable("reason") String reason) {
		log.debug("Starting of the method reject");

		user = updateStatus(id, "R", reason);
		log.debug("Ending of the method reject");
		return new ResponseEntity<User>(user, HttpStatus.OK);

	}

	private User updateStatus(String id, String status, String reason) {
		log.debug("Starting of the method updateStatus");

		log.debug("status: " + status);
		user = userDAO.get(id);

		if (user == null) {
			user = new User();
			user.setErrorCode("404");
			user.setErrorMessage("Could not update the status to " + status);
		} else {

			user.setStatus(status);
			user.setReason(reason);
			
			userDAO.saveOrupdate(user);
			
			user.setErrorCode("200");
			user.setErrorMessage("Updated the status successfully");
		}
		log.debug("Ending of the method updateStatus");
		return user;

	}
	/*@RequestMapping(value = "/myProfile", method = RequestMethod.GET)
	public ResponseEntity<User> myProfile() {
		logger.debug("->->calling method myProfile");
		String loggedInUserID = (String) session.getAttribute("loggedInUserID");
		User user = userDAO.get(loggedInUserID);
		if (user == null) {
			logger.debug("->->->-> User does not exist wiht id" + loggedInUserID);
			user = new User(); // It does not mean that we are inserting new row
			user.setErrorCode("404");
			user.setErrorMessage("User does not exist");
			return new ResponseEntity<User>(user, HttpStatus.NOT_FOUND);
		}
		logger.debug("->->->-> User exist with id" + loggedInUserID);
		logger.debug(user.getName());
		return new ResponseEntity<User>(user, HttpStatus.OK);
	}
*/
	
	
	
	
	
	
	
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public ResponseEntity<User> login(@RequestBody User user, HttpSession session)
	{	
		log.debug("->->->->calling method authenticate");
		
		/*user = userDAO.authenticate(user.getId(), user.getPassword());*/
		log.debug("---->>>>>>the user id is"+user.getId());
		log.debug("---->>>>>>the user password is"+user.getPassword());
		user= userDAO.isValidate(user.getId(), user.getPassword());
		
		if (user == null) 
		{
			user = new User(); // Do wee need to create new user?
			user.setErrorCode("404");
			user.setErrorMessage("Invalid Credentials.  Please enter valid credentials");
			log.debug("->->->->In Valid Credentials");

		} else

		{
			user.setErrorCode("200");
			user.setErrorMessage("You have successfully logged in.");
			user.setIsOnline("Y");
			user.getStatus();
			log.debug("->->->->Valid Credentials");
			/*session.setAttribute("loggedInUser", user);*/
			
			session.setAttribute("loggedInUserID", user.getId());
			session.setAttribute("loggedInUserRole", user.getRole());
			session.setAttribute("LoggedInStatus", user.getStatus());
			
			log.debug("You are loggin with the role : " +session.getAttribute("loggedInUserRole"));
			log.debug("You are loggin with the role : " +session.getAttribute("loggedInUserID"));
			
			log.debug("You are loggin with the role : " +session.getAttribute("LoggedInStatus"));

			friendDAO.setOnline(user.getId());
			userDAO.setOnline(user.getId());
		}

		return new ResponseEntity<User>(user, HttpStatus.OK);
	}

	@RequestMapping(value = "/logout/{id}", method = RequestMethod.GET)
	public ResponseEntity<User> logout(@PathVariable ("id") String id ,HttpSession session) {
		
		log.debug("->->->->calling method logout");
		
		/*String loggedUserID = (String) session.getAttribute("loggedInUserID");*/
				String usrid = id;
		 session.getAttribute(usrid);
		
		log.debug("You are loggin with the role : " +session.getAttribute(usrid));
		
		log.debug("You are loggin with the role : " +session.getAttribute("LoggedInStatus"));

		 /*user = userDAO.get(loggedInUserID);*/
		 
	/*	 SimpleDateFormat sdf = new SimpleDateFormat("dd/mm/yyy : hh:ss");
		 sdf.parse(arg0)*/
		 
		 /*user.setLastSeenTime(new Date(  System.currentTimeMillis()));*/
		/* userDAO.saveOrupdate(user);*/
		 
		
		friendDAO.setOffLine(usrid);
		userDAO.setoffline(usrid);

		session.invalidate();;

		user.setErrorCode("200");
		user.setErrorMessage("You have successfully logged");
		return new ResponseEntity<User>(user, HttpStatus.OK);
	}
	
	
	
		
	

}
	
	
	
	

