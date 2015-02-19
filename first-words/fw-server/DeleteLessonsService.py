from google.appengine.api import users
import webapp2
import json
import urllib

from FWUtils import *
from FWLesson import FWLesson
from FWUser import FWUser

from google.appengine.api import search

class DeleteLessonsService(webapp2.RequestHandler):

    def post(self):
        print "DeleteStreamsService received body = " + self.request.body
        
        allLessons = FWLesson.query().fetch()
        
        if len(allLessons) > 0:
            print 'Fetched ' + str(len(allLessons)) + ' lessons' 
            for lesson in allLessons:
                print 'Deleting lesson = ' + str(lesson.lessonName)
                lesson.key.delete()
        else:
            print 'No lessons to delete '
            
        
     
        '''
        allUsers = FWUser.query().fetch()   
        if len(allUsers) > 0:
            print 'Fetched ' + str(len(allUsers)) + ' users' 
            for fwUser in allUsers:
                print 'Deleting user = ' + str(fwUser.username)
                fwUser.key.delete()
        else:
            print 'No users to delete '
'''
        self.response.set_status(200)
        self.response.headers['Content-Type'] = 'application/json'
        

