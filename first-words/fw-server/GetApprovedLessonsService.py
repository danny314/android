from google.appengine.api import users
import webapp2
import json
import urllib

from FWUtils import *
from FWLesson import FWLesson
from FWUser import FWUser
from LessonOverview import LessonOverview

# Gets all lessons for a user
class GetApprovedLessonsService(webapp2.RequestHandler):

    def post(self):
        print "GetApprovedLessonsService received body = " + self.request.body
        
        jsonObj = getJsonObjFromFormData(self.request.body)
        
        username = jsonObj['username']
        
        fwUsers = FWUser.query(FWUser.username == username).fetch()
        
        lessonViews = []
        approvedLessons = []

        if (len(fwUsers) > 0):
            fwUser = fwUsers[0]
            
            if len(fwUser.approvedLessons) > 0:
                approvedLessons = FWLesson.query(FWLesson.lessonName.IN(fwUser.approvedLessons)).fetch() 
                print 'Found ' + str(len(approvedLessons)) + ' lessons for the user ' + username
    
                for lesson in approvedLessons:
                    
                    lessonOwner = lesson.key.parent().get()
                    
                    ownwership = LESSON_OWNERSHIP_SYSTEM
                    
                    if (lessonOwner.username == username):
                        ownwership = LESSON_OWNERSHIP_SELF
                    elif (lessonOwner.username == SYSTEM_USER):
                        ownwership = LESSON_OWNERSHIP_SYSTEM
                    elif (lesson.shared == True):
                        ownwership = LESSON_OWNERSHIP_PUBLIC
                    else:
                        print 'ERROR: Unapproved lesson ' + lesson.lessonName + ' present in approved lesson list for ' + username
                        
                    lessonView = LessonOverview(lesson.lessonName,lesson.lessonDescription, lesson.createdDate,lesson.lessonType, ownwership, True)
                    lessonViews.append(lessonView)
  
        jsonResponse = to_JSON(lessonViews)
        print jsonResponse
        
        self.response.set_status(200)
        self.response.headers['Content-Type'] = 'application/json'
        
        self.response.out.write(jsonResponse)
        

