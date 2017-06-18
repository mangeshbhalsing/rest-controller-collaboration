package com.niit.collaboration.rest.services;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.niit.collaboration.dao.FriendDAO;
import com.niit.collaboration.dao.UserDAO;
import com.niit.collaboration.model.Friend;


@CrossOrigin("http://localhost:8077")
@RestController
public class FriendController {
	
	private Logger log = LoggerFactory.getLogger(FriendController.class);
	
	@Autowired
	private Friend friend;
	@Autowired
	private FriendDAO friendDAO;
	
	@Autowired
	HttpSession httpSession;
	
	@Autowired
	UserDAO userDAO;
	
	
	//Tested+1
	@RequestMapping(value = "/addFriend/{friendId}/{fid}", method = RequestMethod.GET)
	public ResponseEntity<Friend> addFriend(@PathVariable("friendId") String friendId,@PathVariable ("fid") String fid, HttpSession hs) {

		//String loggedInUserID = fid;
		
		log.debug("This is friendid"+friendId);
		log.debug("This is friendid"+fid);
		if (friendDAO.isRequestAlreadySent(fid, friendId) == true) {
			//friend.setId(0);
			friend.setUserId(fid);
			friend.setFriendId(friendId);
			friend.setStatus("N");
			friend.setIsOnline('N');
			friend.setErrorCode("200");
			friend.setErrorMessage("friend request sent successfully");
			friendDAO.saveFriend(friend);
		} else {
			friend.setErrorCode("404");
			friend.setErrorMessage("friend request already sent!!");
		}
		return new ResponseEntity<Friend>(friend, HttpStatus.OK);
	}

	//Tested+1
	@RequestMapping(value = "/approveFriend/{friendId}/{id}", method = RequestMethod.GET)
	public ResponseEntity<Friend> approveFriend(@PathVariable("friendId") String friendId,@PathVariable ("id") String id, HttpSession hs) {
		
		if (friendDAO.isAlreadyAccepted(id, friendId) == true) {
			/*friend.setErrorCode("200");
			friend.setErrorMessage("Friend request has been accepted!!");
			friend.setUserId(id);
			friend.setFriendId(friendId);
			friend.setIsOnline('N');
			friend.setStatus("N");
			friendDAO.saveFriend(friend);*/
			Friend friend = friendDAO.getFriendToChangeStatus(friendId, id);
			friend.setStatus("A");
			friendDAO.updateFriend(friend);
		} else {
			friend.setErrorCode("404");
			friend.setErrorMessage("Friend request has been already accepted!!");
		}
		return new ResponseEntity<Friend>(friend, HttpStatus.OK);
	}
	
	
	
	/*------------------------------------------------------------*/
	
	
	private Friend updateRequest(String friendID, String status) {
		log.debug("Starting of the method updateRequest");
		String loggedInUserID = (String) httpSession.getAttribute("loggedInUserID");
		log.debug("loggedInUserID : " + loggedInUserID);
		
		if(isFriendRequestAvailabe(friendID)==false)
		{
			friend.setErrorCode("404");
			friend.setErrorMessage("The request does not exist.  So you can not update to "+status);
		}
		
		if (status.equals("A") || status.equals("R"))
			friend = friendDAO.get(friendID, loggedInUserID);
		else
			friend = friendDAO.get(loggedInUserID, friendID);
		
		friend.setStatus(status); // N - New, R->Rejected, A->Accepted

		friendDAO.updateFriend(friend);

		friend.setErrorCode("200");
		/*friend.setErrorMessage(
				"Request from   " + friend.getUserID() + " To " + friend.getFriendID() + " has updated to :" + status);*/
		log.debug("Ending of the method updateRequest");
		return friend;

	}
	
	private boolean isFriendRequestAvailabe(String friendID)
	{
		String loggedInUserID = (String) httpSession.getAttribute("loggedInUserID");
		
		if(friendDAO.get(loggedInUserID,friendID)==null)
			return false;
		else
			return true;
	}
	
	@RequestMapping(value = "/acceptFriend/{friendID}", method = RequestMethod.PUT)
	public ResponseEntity<Friend> acceptFriendFriendRequest(@PathVariable("friendID") String friendID) {
		log.debug("->->->->calling method acceptFriendFriendRequest");
        
		friend = updateRequest(friendID, "A");
		return new ResponseEntity<Friend>(friend, HttpStatus.OK);

	}
	
	@RequestMapping(value = "/unFriend/{friendID}", method = RequestMethod.PUT)
	public ResponseEntity<Friend> unFriend(@PathVariable("friendID") String friendID) {
		log.debug("->->->->calling method unFriend");
		updateRequest(friendID, "U");
		return new ResponseEntity<Friend>(friend, HttpStatus.OK);

	}

	@RequestMapping(value = "/rejectFriend/{friendID}", method = RequestMethod.PUT)
	public ResponseEntity<Friend> rejectFriendFriendRequest(@PathVariable("friendID") String friendID) {
		log.debug("->->->->calling method rejectFriendFriendRequest");

		updateRequest(friendID, "R");
		return new ResponseEntity<Friend>(friend, HttpStatus.OK);

	}
	
	
/*	-----------------------------------------------------------------*/

	//Tested+1
	@RequestMapping(value = "/rejectFriend/{friendId}/{id}", method = RequestMethod.GET)
	public boolean rejectFriend(@PathVariable("friendId") String friendId,@PathVariable ("id") String id, HttpSession hs) {
		//String loggedInUserID = (String) hs.getAttribute(id);
		try {
			friendDAO.deleteFriend(id, friendId);
			
			return true;
		} catch (Exception e) {

			e.printStackTrace();
			return false;
		}
	}
	//Tested+1
	@RequestMapping(value = "/unFriend/{friendId}/{id}", method = RequestMethod.GET)
	public boolean deleteFriend(@PathVariable("friendId") String friendId,@PathVariable ("id") String id, HttpSession hs) {
		//String loggedInUserID = (String) hs.getAttribute(id);
		try {
			friendDAO.removeFriend(id, friendId);
			//friendDAO.removeFriend(friendId, id);
			return true;
		} catch (Exception e) {

			e.printStackTrace();
			return false;
		}
	}

	
	/*-----------------------------------------------------------------*/
	//Tested+1
	@RequestMapping(value = "/pendingFriendRequests/{hid}", method = RequestMethod.GET)
	public ResponseEntity<List<Friend>> pendingFriendRequests(@PathVariable ("hid") String hid,HttpSession session) {
		
		log.debug("->->->->calling method pendingFriendRequest");
		/*String loggedIn= (String) session.getAttribute(hid);
		log.debug("->->->->calling "+session.getAttribute(hid));*/
		List<Friend> pendingFriendList = friendDAO.pendingFriendRequests(hid);
		if (pendingFriendList.isEmpty()) {
			friend.setErrorCode("404");
			friend.setErrorMessage("No new Friend requests");
			//pendingFriendList.add(friend);
		}
		return new ResponseEntity<List<Friend>>(pendingFriendList, HttpStatus.OK);

	}
	//Tested+1
	@RequestMapping(value = "/friends/{id}", method = RequestMethod.GET)
	public ResponseEntity<List<Friend>> friends(@PathVariable ("id") String id ,HttpSession hs) {
		//String loggedInUserID = (String) hs.getAttribute("loggedInUserID");
		List<Friend> friendList = friendDAO.myFriendsList(id);
		if (friendList.isEmpty()) {
			friend.setId(0);
			friend.setErrorCode("404");
			friend.setErrorMessage("No Friends");
			//friendList.add(friend);
		}
		return new ResponseEntity<List<Friend>>(friendList, HttpStatus.OK);

	}

}
