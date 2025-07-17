curl -X POST http://localhost:8080/api/test-scenarios \
  -H "Content-Type: application/json" \
  -d '{
    "name": "网络中断重连测试",
    "description": "模拟网络中断后系统自动重连功能验证",
    "steps": [
      {
        "stepOrder": 1,
        "actionType": "SEND",
        "messageType": "Logon",
        "messageContent": "8=FIX.4.4|9=100|35=A|34=1|49=CLIENT|52=20231115-10:30:00|56=SERVER|98=0|108=30|10=123"
      },
      {
        "stepOrder": 2,
        "actionType": "RECEIVE",
        "messageType": "Logon",
        "timeoutSeconds": 10
      },
      {
        "stepOrder": 3,
        "actionType": "SEND",
        "messageType": "Heartbeat",
        "messageContent": "8=FIX.4.4|9=60|35=0|34=2|49=CLIENT|52=20231115-10:30:05|56=SERVER|10=000"
      },
      {
        "stepOrder": 4,
        "actionType": "NETWORK_INTERRUPT",
        "delayMillis": 30000
      },
      {
        "stepOrder": 5,
        "actionType": "DELAY",
        "delayMillis": 5000
      },
      {
        "stepOrder": 6,
        "actionType": "NETWORK_RESTORE"
      },
      {
        "stepOrder": 7,
        "actionType": "DELAY",
        "delayMillis": 10000
      },
      {
        "stepOrder": 8,
        "actionType": "SEND",
        "messageType": "Heartbeat",
        "messageContent": "8=FIX.4.4|9=60|35=0|34=3|49=CLIENT|52=20231115-10:30:45|56=SERVER|10=000"
      },
      {
        "stepOrder": 9,
        "actionType": "RECEIVE",
        "messageType": "Heartbeat",
        "timeoutSeconds": 15
      },
      {
        "stepOrder": 10,
        "actionType": "ASSERT",
        "assertionExpression": "session.status == 'CONNECTED' && messageCount > 0"
      }
    ]
  }'