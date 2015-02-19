from google.appengine.api import users
import webapp2
import json
import urllib

from FWUtils import *
from FWLesson import FWLesson
from FWUser import FWUser
from LessonOverview import LessonOverview

# Gets all lessons for a user
class GetOwnLessonsService(webapp2.RequestHandler):

    def post(self):
        print "GetOwnLessonsService received body = " + self.request.body
        
        jsonObj = getJsonObjFromFormData(self.request.body)
        
        username = jsonObj['username']
        
        fwUsers = FWUser.query(FWUser.username == username).fetch()
        
        lessonViews = []

        if (len(fwUsers) > 0):
            fwUser = fwUsers[0]
            lessons = FWLesson.query(ancestor=fwUser.key).fetch() 
            print 'Found ' + str(len(lessons)) + ' lessons for the user ' + username
  
            for lesson in lessons:
                print 'Found  lesson ' + lesson.lessonName + ' for user ' + username
                lessonOverview = LessonOverview(lesson.lessonName,lesson.lessonDescription, lesson.createdDate,lesson.lessonType, LESSON_OWNERSHIP_SELF, True)

                lessonViews.append(lessonOverview)
                
        jsonResponse = to_JSON(lessonViews)
        print jsonResponse
        
        self.response.set_status(200)
        self.response.headers['Content-Type'] = 'application/json'
        
        self.response.out.write(jsonResponse)
        

