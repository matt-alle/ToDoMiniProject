# ToDoMiniProject

Matthias Allemann / Roger Dörflinger


Additional Features:

Client:
  - MVC - based on template provided in the course incl. splash screen.
  - GUI to enter user data and connect to server plus entry and display of todo's. A combobox allows quick stepping through the todo-entries.
  - Validates user input and enables/disabled controls accordingly.
  - GUI switches from "logged in" to "logged out" mode by enabling/disabling the corresponding areas.
  - Available in English and German (except for "status").
  
Server:
  - Creates random tokens consisting of letters and numbers to assign to logged in users.
  - Saves todo's and a list of registered users after a client is terminated and restores the data if the server is initiated.
  - Does some basic validation of input data.
  
