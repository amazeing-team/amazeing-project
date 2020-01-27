from django.db import models
from django.db.models.signals import pre_save
from django.dispatch import receiver

class Task(models.Model):
    name = models.CharField(max_length=100)
    description = models.TextField()
    helptext = models.TextField()
    code = models.TextField()
    json_map = models.TextField()
    health = models.IntegerField()
