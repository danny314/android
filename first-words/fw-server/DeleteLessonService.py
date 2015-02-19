import webapp2
import uuid

import cloudstorage as gcs
from google.appengine.ext import blobstore
from google.appengine.api import app_identity, images
from google.appengine.api import search

from FWUtils import *
from FWUser import FWUser
from FWLesson import FWLesson

class DeleteLessonService(webapp2.RequestHandler):

    def post(self):
        print 'Delete Lesson Service post called'
        jsonObj = getJsonObjFromFormData(self.request.body)

        lessonName = jsonObj['lessonName']
        print 'received lesson name = ' + lessonName
        
        username = jsonObj['username']
        print 'received username = ' + username

        print 'received headers=' + str(self.request.headers)
        
        bucket_name = os.environ.get('BUCKET_NAME',app_identity.get_default_gcs_bucket_name())     
        
        print 'bucket name = ' +  str(bucket_name)
        
        for stat in gcs.listbucket("/" + bucket_name + '/' + username + "/" + lessonName):
            print 'stat=' + str(stat)
            try:
                gcs.delete(stat.filename)
                print 'Deleted file ' + stat.filename
            except gcs.NotFoundError:
                print 'ERROR while trying to delete file: No file' + stat.filename
            
        fwUsers = FWUser.query(FWUser.username == username).fetch()
        
        if len(fwUsers) > 0:
            fwUser = fwUsers[0]
            if len(lessonName) > 0:
                fwLessons = FWLesson.query(FWLesson.lessonName == lessonName).fetch()
                
                if len(fwLessons) > 0:
                    fwLesson = fwLessons[0]
                    fwLesson.key.delete()
                    search.Index(name=INDEX_NAME).delete(['doc-' + username + '-' + lessonName.replace(' ','-')])
                    
                    jsonResponse = '{"status":"OK"}'
                    self.response.set_status(200)
                else:
                    self.response.set_status(200)
                    print 'Lesson does not exist!'
                    jsonResponse = '{"status":"FAILED","error":"NO_SUCH_LESSON"}'
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
            
        
