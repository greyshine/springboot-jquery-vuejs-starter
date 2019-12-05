Syntax for an email template:
=============================
First line is subject

This is main content after first line and is html content.
It will be trimmed at begin and end.
A blank line after first, subject line is not needed but more readable.

A colon in a separate line will terminate the html part and text part will continue if non blank text is provided.
No colon implied no text content.  

Any line beginning with a colon and not just containing only a colon as first character will be given out after the colon.  
In case you need a simple colon as first character write the following:
--
Got it? It is a simple colon.

An html block or text block is trimmed.

For placing variables use double bracelets {{var}}


-
This is plain text content.