from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('task', '0004_task_json_map'),
    ]

    operations = [
        migrations.AddField(
            model_name='task',
            name='map_input',
            field=models.TextField(default=('#___#',)),
            preserve_default=False,
        ),
    ]
