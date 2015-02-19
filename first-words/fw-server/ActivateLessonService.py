import webapp2
import uuid

import cloudstorage as gcs
from google.appengine.ext import blobstore
from google.appengine.api import app_identity, images

from FWUtils import *
from FWUser import FWUser
from FWLesson import FWLesson

class ActivateLessonService(webapp2.RequestHandler):

    def post(self):
        print 'ActivateLesson Service post called'
        jsonObj = getJsonObjFromFormData(self.request.body)

        lessonName = jsonObj['lessonName']
        print 'received lesson name = ' + lessonName
        
        username = jsonObj['username']
        print 'received username = ' + username

        fwUsers = FWUser.query(FWUser.username == username).fetch()
        
        if len(fwUsers) > 0:
            fwUser = fwUsers[0]
            if len(lessonName) > 0:
                fwLessons = FWLesson.query(FWLesson.lessonName == lessonName).fetch()
                fwLesson = fwLessons[0]
                fwLesson.active = True
                fwLesson.put()
                jsonResponse = '{"status":"OK"}'
                self.response.set_status(200)
            else:
                self.response.set_status(400)
                print 'No lesson received!'
                jsonResponse = '{"status":"FAILED","error":"NO_LESSON_DATA"}'
        else:
            self.response.set_status(400)
            print 'Invalid user!'
            jsonResponse = '{"status":"FAILED","error":"INVALID_USER"}'
            
                
        
        self.response.headers['Content-Type'] = 'application/json'
        self.response.out.write(jsonResponse)
            
        