from google.appengine.api import users
import webapp2
import json
import urllib

from FWUtils import *
from FWLesson import FWLesson
from FWUser import FWUser
from LessonOverview import LessonOverview

# Gets all lessons for a user without the contents
class GetUserLessonsService(webapp2.RequestHandler):

    def post(self):
        print "GetStreamsService received body = " + self.request.body
        
        jsonObj = getJsonObjFromFormData(self.request.body)
        
        username = jsonObj['username']
        
        fwUsers = FWUser.query(FWUser.username == username).fetch()
        
        mergedLessons = []

        if (len(fwUsers) > 0):
            fwUser = fwUsers[0]
            lessons = FWLesson.query(ancestor=fwUser.key).fetch() 
            print 'Found ' + str(len(lessons)) + ' lessons for the user ' + username
        
            approvedLessons = []
            
            if len(fwUser.approvedLessons) > 0:
                
                approvedLessons = FWLesson.query(FWLesson.lessonName.IN(fwUser.approvedLessons)).fetch() 
                print 'Found ' + str(len(approvedLessons)) + ' approved lessons for the user ' + username
                
                for approvedLesson in approvedLessons:
                    
                    lessonOwner = approvedLesson.key.parent().get()
                    
                    ownwership = LESSON_OWNERSHIP_SYSTEM
                    
                    if (lessonOwner.username == username):
                        ownwership = LESSON_OWNERSHIP_SELF
                    elif (lessonOwner.username == SYSTEM_USER):
                        ownwership = LESSON_OWNERSHIP_SYSTEM
                    elif (approvedLesson.shared == True):
                        ownwership = LESSON_OWNERSHIP_PUBLIC
                    else:
                        print 'ERROR: Unapproved lesson ' + approvedLesson.lessonName + ' present in approved lesson list for ' + username
                    
                    
                    lessonOverview = LessonOverview(approvedLesson.lessonName,approvedLesson.lessonDescription, approvedLesson.createdDate,approvedLesson.lessonType, ownwership, True)
                    
                    mergedLessons.append(lessonOverview)
                
            for ownLesson in lessons:
                if ownLesson not in approvedLessons:
                    print 'adding lesson ' + ownLesson.lessonName
                    lessonOverview = LessonOverview(ownLesson.lessonName,ownLesson.lessonDescription, ownLesson.createdDate,ownLesson.lessonType, LESSON_OWNERSHIP_SELF, False)
                    mergedLessons.append(lessonOverview) 
                 
            print 'Merged list size ' + str(len(mergedLessons))
            
        jsonResponse = to_JSON(mergedLessons)
        print jsonResponse
        
        self.response.set_status(200)
        self.response.headers['Content-Type'] = 'application/json'
        
        self.response.out.write(jsonResponse)
        

