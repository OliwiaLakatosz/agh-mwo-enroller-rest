package com.company.enroller.controllers;

import com.company.enroller.model.Meeting;
import com.company.enroller.model.Participant;
import com.company.enroller.persistence.MeetingService;
import com.company.enroller.persistence.ParticipantService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("/meetings")
public class MeetingRestController {

    @Autowired
    MeetingService meetingService;

    @Autowired
    ParticipantService participantService;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<?> getMeetings() {
        Collection<Meeting> meetings = meetingService.getAll();
        return new ResponseEntity<Collection<Meeting>>(meetings, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> getMetting(@PathVariable("id") long id) {
        Meeting meeting = meetingService.getMeetingById(id);
        if (meeting == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<Meeting>(meeting, HttpStatus.OK);
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResponseEntity<?> addMeeting(@RequestBody Meeting meeting) {
        Meeting addedMeeting = meetingService.getMeetingByTitle(meeting.getTitle());
        if (addedMeeting != null) {
            return new ResponseEntity<>("Unable to create. A meeting named " + meeting.getTitle() + " already exist.", HttpStatus.CONFLICT);
        }

        meetingService.add(meeting);
        return new ResponseEntity<Meeting>(meeting, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteMeeting(@PathVariable("id") long id) {
        Meeting meeting = meetingService.getMeetingById(id);
        if (meeting == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        meetingService.delete(meeting);
        return new ResponseEntity<Meeting>(meeting, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity<?> updateMeeting(@PathVariable("id") long id,
                                           @RequestBody Meeting incomingMeeting) {
        Meeting meeting = meetingService.getMeetingById(id);
        if (meeting == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        meeting.setDate(incomingMeeting.getDate());
        meeting.setDescription(incomingMeeting.getDescription());
        meeting.setTitle(incomingMeeting.getTitle());
        meetingService.update(meeting);
        return new ResponseEntity<Meeting>(meeting, HttpStatus.OK);
    }

    @RequestMapping(value = "/{meetingId}", method = RequestMethod.POST)
    public ResponseEntity<?> addParticipantToMeeting(@PathVariable("meetingId") long meetingId,
                                                     @RequestBody Participant participant) {
        Meeting meeting = meetingService.getMeetingById(meetingId);
        if (meeting == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        if (participant == null) {
            return new ResponseEntity<>("Participant with this login does not exist.", HttpStatus.NOT_FOUND);
        }

        meetingService.addParticipantToMeeting(meetingId, participant);

        return new ResponseEntity<Meeting>(meeting, HttpStatus.OK);
    }

    @RequestMapping(value = "/{meetingId}", method = RequestMethod.DELETE)
    public ResponseEntity<?> removeParticipantFromMeeting(@PathVariable("meetingId") long meetingId,
                                                     @RequestBody Participant participant) {
        Meeting meeting = meetingService.getMeetingById(meetingId);
        if (meeting == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        if (participant == null) {
            return new ResponseEntity<>("Participant with this login does not exist.", HttpStatus.NOT_FOUND);
        }

        //TODO: Implement in meetingService and return
        return null;
    }

    @RequestMapping(value = "/{meetingId}/participants", method = RequestMethod.GET)
    public ResponseEntity<?> getMeetingParticipants(@PathVariable("meetingId") long meetingId) {

        Collection<Participant> participants = meetingService.getMeetingParticipants(meetingId);
        if (participants == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        //TODO: For now, participant login "must be" unique in table meeting_participant in DB.
        //TODO: Change it so one Participant can go to multiple meetings (is it needed?)
        return new ResponseEntity<Collection<Participant>>(participants, HttpStatus.OK);
    }
}
