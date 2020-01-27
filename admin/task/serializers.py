from .models import Task
from rest_framework import serializers

class TaskSerializer(serializers.HyperlinkedModelSerializer):
    class Meta:
        model = Task
        fields = ['id','name', 'description', 'helptext', 'code', 'json_map', 'health']
