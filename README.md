# CubeLogic Interview Coding challenge

## Overview
This is the code base for an interview requested by CubeLogic (https://cubelogic.com/)

This will include one Interface, which has only one method: it takes a list of trades and a list of orders, and outputs those of them which it finds suspicious.

## Assumptions

- Trade and Order price has been changed to BigDecimal to ensure accuracy and account for larger numbers
- Trades and Orders will always have prices, timestamps and sides assigned and not null
- Trades can be negative


## Original requested task description

TEST Overview:

“Dear candidate, thanks a lot for your interest in the role and we wish you the best of luck.
This is a coding challenge, we expect it takes no longer than an hour, but there is no time control – feel free to take as much as you find it’s necessary.
Send the solution in either email attachment or post it to your GitHub and provide a link, or any other way you find convenient.
While coding, pay attention to naming, structures clarity, overall readability, think of corner cases, and it would be great to see some tests.
Use Java and any libraries but try to keep things as simple as possible.
If you find something missing in the task description, make reasonable assumptions and put them in the Readme file.”

TASK:
“Define and implement one Interface, which has only one method: it takes a list of trades and a list of orders, and outputs those of them which it finds suspicious.
Trades and orders both have following fields: long id, double price (it may go negative on our marketplace), double volume, Side side (buy or sell), LocalDateTime timestamp.
You find trades and orders suspicious if you see the following pattern in the trader's behaviour:
In a time, window of 30 minutes before the trade, there were placed order(s) of an opposite side.
The orders you are checking for the trade have a price not more than 10% lower or higher than the trade price (depending on the side: if it's a buy trade, then sell orders should be not more than 10% more expensive, and vice versa)
I.e. we are trying to catch that the trader was attempting to make the market moving to a better price.”