ast(Tokens, Out, Options) :-
    catch(
        (
            maplist,
            debug(ast, "Renamed", [Renamed])
        )
    ).