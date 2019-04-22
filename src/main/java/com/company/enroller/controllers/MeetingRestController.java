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
            return new ResponseEntity<>("Meeting does not exist.",HttpStatus.NOT_FOUND);
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
            return new ResponseEntity<>("Meeting does not exist.", HttpStatus.NOT_FOUND);
        }
        meetingService.delete(meeting);
        return new ResponseEntity<Meeting>(meeting, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity<?> updateMeeting(@PathVariable("id") long id,
                                           @RequestBody Meeting incomingMeeting) {
        Meeting meeting = meetingService.getMeetingById(id);
        if (meeting == null) {
            return new ResponseEntity<>("Meeting does not exist.", HttpStatus.NOT_FOUND);
        }
        meeting.setDate(incomingMeeting.getDate());
        meeting.setDescription(incomingMeeting.getDescription());
        meeting.setTitle(incomingMeeting.getTitle());
        meetingService.update(meeting);
        return new ResponseEntity<Meeting>(meeting, HttpStatus.OK);
    }

    @RequestMapping(value = "/{meetingId}/participants/{participantId}", method = RequestMethod.POST)
    public ResponseEntity<?> addParticipantToMeeting(@PathVariable("meetingId") long meetingId,
                                                     @PathVariable("participantId") String participantId) {
        Meeting meeting = meetingService.getMeetingById(meetingId);
        Participant participant = participantService.findByLogin(participantId);
        if (meeting == null) {
            return new ResponseEntity<>("Meeting does not exist.", HttpStatus.NOT_FOUND);
        }
        if (participant == null) {
            return new ResponseEntity<>("Participant with this login does not exist.", HttpStatus.NOT_FOUND);
        }

        meetingService.addParticipantToMeeting(meetingId, participant);

        return new ResponseEntity<Meeting>(meeting, HttpStatus.OK);
    }

    @RequestMapping(value = "/{meetingId}/participants/{participantId}", method = RequestMethod.DELETE)
    public ResponseEntity<?> removeParticipantFromMeeting(@PathVariable("meetingId") long meetingId,
                                                     @PathVariable("participantId") String participantId) {
        Meeting meeting = meetingService.getMeetingById(meetingId);
        Participant participant = participantService.findByLogin(participantId);
        if (meeting == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        if (participant == null) {
            return new ResponseEntity<>("Participant with this login does not exist.", HttpStatus.NOT_FOUND);
        }

        meetingService.removeParticipantFromMeeting(meetingId, participant);
        meetingService.update(meeting);
        return new ResponseEntity<Meeting>(meeting, HttpStatus.OK);
    }

    @RequestMapping(value = "/{meetingId}/participants", method = RequestMethod.GET)
    public ResponseEntity<?> getMeetingParticipants(@PathVariable("meetingId") long meetingId) {

        Collection<Participant> participants = meetingService.getMeetingParticipants(meetingId);
        if (participants == null) {
            return new ResponseEntity<>("Couldn't find any participants for meeting with id=" + meetingId, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<Collection<Participant>>(participants, HttpStatus.OK);
    }

    @RequestMapping(value = "/sorted/title", method = RequestMethod.GET)
    public ResponseEntity<?> getMeetingsSortedByTitle() {
        Collection<Meeting> sorted = meetingService.sortByTitle();
        return new ResponseEntity<Collection<Meeting>>(sorted, HttpStatus.OK);
    }

    @RequestMapping(value = "/search/participant", method = RequestMethod.GET)
    public  ResponseEntity<?> searchMeetingsByParticipant(@RequestParam String id) {
        Participant participant = participantService.findByLogin(id);
        if (participant == null) {
            return new ResponseEntity<>("Participant with login " + id + " doesn't exist.", HttpStatus.NOT_FOUND);
        }

        Collection<Meeting> meetings = meetingService.searchMeetingsByParticipant(id);
        if (meetings.size() == 0) {
            return new ResponseEntity<>("Meeting does not exist.", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<Collection<Meeting>>(meetings, HttpStatus.OK);
    }

    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public ResponseEntity<?> searchMeetingsByAttributes(@RequestParam String title,
                                                   @RequestParam(required = false) String description) {
        Collection<Meeting> found = meetingService.searchMeetingByAttribute(title, description);
        if (found.size() == 0) {
            return new ResponseEntity<>("Meeting with given title/description does not exits", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<Collection<Meeting>>(found, HttpStatus.OK);
    }
}
