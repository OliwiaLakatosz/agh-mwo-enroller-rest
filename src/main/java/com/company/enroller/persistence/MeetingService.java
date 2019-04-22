package com.company.enroller.persistence;

import java.util.Collection;

import com.company.enroller.model.Participant;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Component;

import com.company.enroller.model.Meeting;

@Component("meetingService")
public class MeetingService {

	DatabaseConnector connector;

	public MeetingService() {
		connector = DatabaseConnector.getInstance();
	}

	public Collection<Meeting> getAll() {
//		String hql = "FROM Meeting";
//		Query query = connector.getSession().createQuery(hql);
		return connector.getSession().createCriteria(Meeting.class).list();
	}

    public Meeting getMeetingById(long id) {
	    return (Meeting) connector.getSession().get(Meeting.class, id);
    }

    public Meeting getMeetingByTitle(String title) {
        Criteria criteria = connector.getSession().createCriteria(Meeting.class);
        return (Meeting) criteria.add(Restrictions.eq("title", title)).uniqueResult();
    }

    public void add(Meeting meeting) {
        Transaction transaction = connector.getSession().beginTransaction();
        connector.getSession().save(meeting);
        transaction.commit();
    }

    public void delete(Meeting meeting) {
	    Transaction transaction = connector.getSession().beginTransaction();
	    connector.getSession().delete(meeting);
	    transaction.commit();
    }

    public void update(Meeting meeting) {
        Transaction transaction = connector.getSession().beginTransaction();
        connector.getSession().update(meeting);
        transaction.commit();
    }

    public Collection<Participant> getMeetingParticipants(long meetingId) {
	    Meeting meeting = this.getMeetingById(meetingId);
	    return meeting.getParticipants();
    }

    public void addParticipantToMeeting(long id, Participant participant) {
	    Transaction transaction = connector.getSession().beginTransaction();
	    Meeting meeting = this.getMeetingById(id);
	    meeting.addParticipant(participant);
	    connector.getSession().save(meeting);
	    connector.getSession().save(participant);
	    transaction.commit();
    }

    public void removeParticipantFromMeeting(long meetingId, Participant participant) {
	    Transaction transaction = connector.getSession().beginTransaction();
	    Meeting meeting = this.getMeetingById(meetingId);
	    meeting.removeParticipant(participant);
	    connector.getSession().save(meeting);
	    connector.getSession().save(participant);
	    transaction.commit();
    }

    public Collection<Meeting> sortByTitle() {
        String hql = "FROM Meeting M ORDER BY M.title";
        Query query = connector.getSession().createQuery(hql);
        return query.list();
    }

    public Collection<Meeting> searchMeetingsByParticipant(String login) {
        String hql = "SELECT m FROM Meeting m JOIN m.participants p WHERE p.login LIKE ?";
        Query query = connector.getSession().createQuery(hql);
        query.setParameter(0, login);
        return query.list();
    }

    public Collection<Meeting> searchMeetingByAttribute(String type, String attribute) {
        Criteria criteria = connector.getSession().createCriteria(Meeting.class);
        StringBuilder sb = new StringBuilder(attribute);
        sb.append("%");
        sb.insert(0, "%");

        if (type.equals("title")) {
            criteria.add(Restrictions.like("title", sb.toString()));
        }
        if (type.equals("description")) {
            criteria.add(Restrictions.like("description", sb.toString()));
        }

        return (Collection<Meeting>) criteria.list();
    }
}
