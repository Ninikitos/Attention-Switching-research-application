import json
from channels.generic.websocket import AsyncWebsocketConsumer


class RoundConsumer(AsyncWebsocketConsumer):
    def __init__(self, *args, **kwargs):
        super().__init__(args, kwargs)
        self.room_group_name = None
        self.session_id = None

    async def connect(self):
        self.session_id = self.scope['url_route']['kwargs']['session_id']
        self.room_group_name = f'session_{self.session_id}'

        await self.channel_layer.group_add(
            self.room_group_name,
            self.channel_name
        )

        await self.accept()

        await self.send(text_data=json.dumps({
            'type': 'connected',
            'message': 'Connected to session',
            'session_id': str(self.session_id)
        }))

    async def disconnect(self, close_code):
        await self.channel_layer.group_discard(
            self.room_group_name,
            self.channel_name
        )

    async def receive(self, text_data):
        data = json.loads(text_data)
        message_type = data.get('type')

        if message_type == 'ping':
            await self.send(text_data=json.dumps({
                'type': 'pong'
            }))

    async def round_updated(self, event):
        await self.send(text_data=json.dumps({
            'type': 'round_updated',
            'current_round': event['current_round'],
            'mobile_matrix': event['mobile_matrix'],
            'is_completed': event.get('is_completed', False)
        }))

    async def session_completed(self, event):
        await self.send(text_data=json.dumps({
            'type': 'session_completed',
            'message': 'All rounds completed'
        }))

    async def session_stop(self, event):
        await self.send(text_data=json.dumps({
            'type': 'session_stop',
            'message': 'Session was stopped.'
        }))