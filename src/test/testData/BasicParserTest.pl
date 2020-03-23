len(0, []).
len(Len, [_|Tail]) :- len(Lsub, Tail), Len is Lsub + 1.

sum(0, []).
sum(Sum, [Head|Tail]) :- sum(LSum, Tail), Sum is LSum + Head.

avg(Avg, List) :- len(Len, List), sum(Sum, List), Avg is Sum/Len.

append([], Item, Out) :- Out = [Item].
append([Single], Item, Out) :- Out = [Single, Item].
append([Head|Tail], Item, Out) :- append(Tail, Item, Ret), Out = [Head|Ret].

concat([], List, List).
concat(List, [], List).
%concat(List, [Head|Tail], Out) :- append(List, Head, Ret), concat(Ret, Tail, Value), Out = Value.
concat([Head|Tail], List, [Head|Tail1]) :- concat(Tail, List, Tail1).

fib(N, R) :- fib(N, 0, 1, R).
fib(0, C1, _, C1) :- !.
fib(N, C1, C2, R) :-
	C3 is C1 + C2,
	N1 is N - 1,
	fib(N1, C2, C3, R).

fact(N, R) :- fact(N, 1, R).

fact(N, C, R) :-
	N1 is N - 1,
	C1 is C * N,
	fact(N1, C1, R).

fact(0, R, R) :- !.