from django.db import models
import uuid


class Session(models.Model):
    session_id = models.UUIDField(primary_key=True, default=uuid.uuid4, editable=False)
    created_at = models.DateTimeField(auto_now_add=True)
    current_round = models.IntegerField(default=0)
    is_active = models.BooleanField(default=True)

    def __str__(self):
        return f"Session {self.created_at}"

    class Meta:
        ordering = ['-created_at']


class Round(models.Model):
    session = models.ForeignKey(Session, on_delete=models.CASCADE, related_name='rounds')
    round_number = models.IntegerField()
    web_matrix = models.JSONField()
    mobile_matrix = models.JSONField()
    target_letters = models.JSONField()
    start_time = models.DateTimeField(auto_now_add=True)
    end_time = models.DateTimeField(null=True, blank=True)
    is_completed = models.BooleanField(default=False)

    # User choice
    web_selection = models.JSONField(null=True, blank=True)

    # Results
    is_correct = models.BooleanField(null=True, blank=True)
    response_time = models.FloatField(null=True, blank=True)

    def __str__(self):
        return f"Round {self.round_number} of Session {self.session.session_id}"

    class Meta:
        ordering = ['round_number']
        unique_together = ['session', 'round_number']


class Statistics(models.Model):
    session = models.OneToOneField(Session, on_delete=models.CASCADE, related_name='statistics')
    total_time = models.FloatField(default=0.0)
    average_time = models.FloatField(default=0.0)
    mistakes_count = models.IntegerField(default=0)
    correct_count = models.IntegerField(default=0)

    def __str__(self):
        return f"Statistics for {self.session.session_id}"

    class Meta:
        verbose_name_plural = "Statistics"