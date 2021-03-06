from google.appengine.api import users
import webapp2
import json
import urllib

from FWUtils import *
from FWLesson import FWLesson
from FWUser import FWUser
from LessonView import LessonView

from google.appengine.api import search

class GetLessonsService(webapp2.RequestHandler):

    def post(self):
        print "GetStreamsService received body = " + self.request.body
        
        jsonObj = getJsonObjFromFormData(self.request.body)
        
        username = jsonObj['username']
        
        fwUsers = FWUser.query(FWUser.username == username).fetch()
        
        if (len(fwUsers) > 0):
            fwUser = fwUsers[0]
            lessons = FWLesson.query(ancestor=fwUser.key).fetch() 
            print 'Found ' + str(len(lessons)) + ' lessons for the user ' + username
  
            lessonViews = []
            
            for lesson in lessons:
                lessonView = LessonView(lesson.lessonName, lesson.lessonContents)
                lessonViews.append(lessonView)
                
        jsonResponse = to_JSON(lessonViews)
        print jsonResponse
        
        self.response.set_status(200)
        self.response.headers['Content-Type'] = 'application/json'
        
        self.response.out.write(jsonResponse)
        

