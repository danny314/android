from google.appengine.api import users
import webapp2
import json
import urllib

from FWUtils import *
from FWLesson import FWLesson
from FWUser import FWUser
from LessonView import LessonView

# Gets single lesson by name
class GetSingleLessonService(webapp2.RequestHandler):

    def post(self):
        print "GetSingleLessonService received body = " + self.request.body
        
        jsonObj = getJsonObjFromFormData(self.request.body)
        
        username = jsonObj['username']
        lessonName = jsonObj['lessonName']
        
        fwUsers = FWUser.query(FWUser.username == username).fetch()
        
        if (len(fwUsers) > 0):
            lessons = FWLesson.query(FWLesson.lessonName == lessonName).fetch() 
            print 'Found ' + str(len(lessons)) + ' lessons for the user ' + username
  
            if (len(lessons) > 0):
                lesson = lessons[0]
                lessonView = LessonView(lesson.lessonName,lesson.lessonDescription, lesson.lessonContents
                                        , lesson.createdDate,lesson.lessonType)
                self.response.set_status(200)
                jsonResponse = to_JSON(lessonView)
            else:
                self.response.set_status(400)
                print 'Bad lesson name!'
                jsonResponse = '{"status":"FAILED","error":"NO_SUCH_LESSON"}'
        else:
            self.response.set_status(400)
            print 'Invalid user!'
            jsonResponse = '{"status":"FAILED","error":"INVALID_USER"}'
                            
        print jsonResponse
        
        
        self.response.headers['Content-Type'] = 'application/json'
        self.response.out.write(jsonResponse)
        

