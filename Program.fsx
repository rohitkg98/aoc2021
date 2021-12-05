let day1 () =
    let input =
        System.IO.File.ReadAllText "./src/day1/input.txt"

    let scans = input.Split '\n' |> Array.map int

    scans
    |> Array.pairwise
    |> Array.map (fun (pred, succ) -> if pred > succ then 0 else 1)
    |> Array.sum
    |> printfn "Total Increased Scans %d"

    Array.mapi (fun i v -> printfn "%d, %d" v scans[i+3]; if v >= scans[i+3] then 0 else 1) scans[..scans.Length-4]
    |> Array.sum
    |> printfn "Total Sliding Window Increased Scans %d"


let codeBindings = Map [ ("1", day1) ]

[<EntryPoint>]
let main args =
    if args.Length = 0 then
        failwith "Please enter the day to execute"

    match Map.tryFind args.[0] codeBindings with
    | None -> failwith "Provide a valid day to execute"
    | Some dayFunc -> dayFunc ()

    0

