import webapp2
import uuid

import cloudstorage as gcs
from google.appengine.ext import blobstore
from google.appengine.api import app_identity, images

from FWUtils import *
from FWUser import FWUser
from FWLesson import FWLesson

class UpdateLessonService(webapp2.RequestHandler):

    def post(self):
        print 'Update Lesson Service post called'
        image = self.request.get('img')
        
        print 'received image with length ' + str(len(image))

        lessonName = self.request.get('lessonName')
        print 'received lesson name = ' + lessonName
        
        username = self.request.get('username')
        print 'received username = ' + username

        currentLetter = self.request.get('currentLetter')
        print 'received current letter = ' + currentLetter

        currentWord = self.request.get('currentWord')
        print 'received current word = ' + currentWord

        postedFileName = self.request.get('fileName')
        print 'postedFileName = ' + postedFileName
        
        if (postedFileName == ''):
            postedFileName = str(uuid.uuid4())
        else:
            pass    

        print 'received headers=' + str(self.request.headers)
        
        bucket_name = os.environ.get('BUCKET_NAME',app_identity.get_default_gcs_bucket_name())     
        
        print 'bucket name = ' +  str(bucket_name)
           
        filename = '/' + bucket_name + '/' + username + "/" + lessonName + '/' + postedFileName
        
        print 'full file path = ' + filename
         
        gcs_file = gcs.open(filename,'w',content_type='image/png')
        gcs_file.write(image)
        gcs_file.close()
        
        gcs_file = gcs.open(filename)
        servingUrl = images.get_serving_url(blobstore.create_gs_key('/gs' + filename), secure_url=False)
        
        print 'servingUrl = ' +  servingUrl
        #print 'reading file... ' + gcs_file.read()
        gcs_file.close()        
        
        fwUsers = FWUser.query(FWUser.username == username).fetch()
        
        if len(fwUsers) > 0:
            fwUser = fwUsers[0]
            if len(lessonName) > 0:
                fwLessons = FWLesson.query(FWLesson.lessonName == lessonName).fetch()
                fwLesson = fwLessons[0]
                newLessonContents = fwLesson.lessonContents
                newLessonContents[currentLetter] = {"word":currentWord,"imageUrl":servingUrl}
                print 'new lesson contents = ' + str(newLessonContents)
                fwLesson.lessonContents = newLessonContents
                print 'saved lesson contents = ' + str(fwLesson.lessonContents)
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
            
        