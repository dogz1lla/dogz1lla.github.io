# How to sort a list of dictionaries in python
Ever needed to sort a table in pandas over several columns?
It is straighforward to do using _.sort\_values_ method. 
But what if you want to sort a list of vanilla python dictionaries by one or 
more keys?
This can be conveniently done using standard python library.

## _Sorted_ is your friend
[sorted](https://docs.python.org/3/library/functions.html#sorted) is part of 
standard python library that allows to sort iterables.
One can specify what the elements of the iterable should be sorted by using 
_key_ argument of _sorted_.

## Example data
Here is a hypothetical list of dictionaries we would like to sort.
It is a collection of user data, some of the entries have a placeholder for the 
name field ("John Doe"):
```python
data = [
    dict(name="Bruce Wayne", age=32), 
    dict(name="Tony Stark", age=38), 
    dict(name="John Doe", age=42), 
    dict(name="John Doe", age=27), 
]
```

## Sorting function
Lets create a function that takes a list of dictionaries to sort and an iterable 
that contains keys of each dictionary in the order in which we want to 
prioritize the sorting:
```python
def sort_list_dict(data: list[dict], *keys) -> list[dict]:
    sorting_fn = lambda d: tuple([d[k] for k in keys])
    return sorted(data, key=sorting_fn)
```
The secret sauce is to use an anonymous function that takes a dictionary and 
returns a /tuple/ of values corresponding to the desired keys.

## Full example
```python
def sort_list_dict(data: list[dict], *keys) -> list[dict]:
    sorting_fn = lambda d: tuple([d[k] for k in keys])
    return sorted(data, key=sorting_fn)

def test():
    data = [
        dict(name="Bruce Wayne", age=32), 
        dict(name="Tony Stark", age=38), 
        dict(name="John Doe", age=42), 
        dict(name="John Doe", age=27), 
    ]
    print(f"Initially: {data}")
    keys = ("name", )
    sorted_data = sort_list_dict(data, *keys)
    print(f"Sorted by name: {sorted_data}")
    keys = ("name", "age")
    sorted_data = sort_list_dict(data, *keys)
    print(f"Sorted by name and age: {sorted_data}")

def main():
    test()

if __name__ == "__main__":
    main()
```

Resulting output would look something like this:
```bash
# initially:
[
  {'name': 'Bruce Wayne', 'age': 32},
  {'name': 'Tony Stark', 'age': 38},
  {'name': 'John Doe', 'age': 42},
  {'name': 'John Doe', 'age': 27},
]

# sorted by name:
[
  {'name': 'Bruce Wayne', 'age': 32},
  {'name': 'John Doe', 'age': 42},
  {'name': 'John Doe', 'age': 27},
  {'name': 'Tony Stark', 'age': 38},
]

# sorted by name and age:
[
  {'name': 'Bruce Wayne', 'age': 32},
  {'name': 'John Doe', 'age': 27},
  {'name': 'John Doe', 'age': 42},
  {'name': 'Tony Stark', 'age': 38},
]
```
