package com.niit.collaboration.rest.services;

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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.niit.collaboration.dao.EventDAO;
import com.niit.collaboration.model.Event;

@CrossOrigin("http://localhost:8077")
@RestController
public class EventRestService {
	
	private Logger log = LoggerFactory.getLogger(EventRestService.class);
	
	@Autowired
	private Event event;
	
	@Autowired
	private EventDAO eventDAO;
	
	@Autowired
	private HttpSession session;
	
	@GetMapping("/helloSirEvent")
	public String sayHello()
	{
		return "  Hello from User rest service Modifed message";
	}
	
	/*To Create a Event In Rest services  Tested*/
	
	@RequestMapping(value = "/CreateEvent", method = RequestMethod.POST)
	public boolean Createevent(@RequestBody Event event) {
		// event.setStatus('Y');  ///bydefault we have to set the initial status will be 'N"

		if (eventDAO.save(event)) {
			
			event.setErrorCode("200");
			event.setErrorMessage("Seccessfully posted!");
			return true;
		} else {
			
			event.setErrorCode("404");
			event.setErrorMessage("Not registered!");
			return false;

		}

	}
	
	/*To fetch the Event with Help of Id*/
	
	@RequestMapping(value ="/event/{id}", method = RequestMethod.GET)
	public ResponseEntity<Event> getEvent(@PathVariable("id") String id){
		Event event1 =  eventDAO.getEventById(id);
		if(event1 == null){
		    event1 = new Event();
		    event1.setId(0);
			event1.setErrorCode("404");
			event1.setErrorMessage("No Events posted!");
		}
		return new ResponseEntity<Event>(event1, HttpStatus.OK);
	}
	
	/*To fetch the All The Event*/
	
	@RequestMapping(value = "/events", method = RequestMethod.GET)
	public ResponseEntity<List<Event>> getEventList() {
		List<Event> eventList = eventDAO.list();
		if (eventList.isEmpty()) {
			Event event = new Event();
			event.setErrorCode("404");
			event.setErrorMessage("Events are not available");
			eventList.add(event);
		}
		return new ResponseEntity<List<Event>>(eventList, HttpStatus.OK);
	}
	
	
	/*To Delete the Event With Id*/
	
	@RequestMapping(value= "/deleteEvent/{id}",  method = RequestMethod.GET)
	public boolean deleteEvent(@PathVariable("id") String id){
		
		
		if(eventDAO.getEventById(id) == null){
			Event event = new Event();
			event.setErrorCode("404");
			event.setErrorMessage("No Events posted!");
		}
		eventDAO.deleteById(id);
		return true ;
	}
	
	
	//Tested
	
	/*To Update the Event with Id*/
	
	@RequestMapping(value = "/updateEvent/{id}", method = RequestMethod.POST)
	public ResponseEntity<Event> updateEvent(@PathVariable("id") String id, @RequestBody Event event){
		if(eventDAO.getEventById(id) == null){
			event.setErrorCode("404");
			event.setErrorMessage("No Events posted!");
		}else{
			eventDAO.update(event);
			event.setErrorCode("200");
			event.setErrorMessage("Saved the Event Successfully!");
			}
		return new ResponseEntity<Event>(event, HttpStatus.OK);
	}
	
	

}
