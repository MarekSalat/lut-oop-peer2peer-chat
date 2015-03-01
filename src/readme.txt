Author: Marek Salát

see --help to more info about params and commands

for better understanding what is going on, program was left in verbose mode when you can see this marks:

|--> ...  outgoing request
<--| ...  incoming request or response
|--| ...  message from client

How does it works?

When you run the program without boost peer (you are the first user in this chat). You have to wait for other users.
When you know other user and you run the program with boost peer parameter. Client will download route table from
known boost peer. After receiving route table the client will send echo message
to every chat client in route table. When you receive echo message from unknown client, this client will be added
to your route table. Other clients can also ask you for route table. Every client has route table of all
participants in peer2peer char. If you close the client, it will send message to all other clients you are offline. You
can also change your visibility state but nobody can send you message.


How does code works and what is chat api and chat protocol?

I wanted to make every class smallest as possible with single responsibility.
I have peer2peer chat protocol, which handles incoming messages and is able to send request. If you want add new
protocol feature, only you need to do is implements ChatApiCallback and you have to register it before protocol is started.

Then I have peer2peer chat api, which manages client command (input from user). When you need to add new user command
just implement ChatApiCallback and register it before start. This is all.

ChatApi contains simple minimalistic version of protocol (update route table, sending messages, sending echo, receiving
messages and other simples commands) and chat api (showing list of users, state changing, sending messages and so on).
GroupChatApi is extension which add group chat support.


What is ChatRequest?

It is only envelop for communication between chat and yours/mine api.