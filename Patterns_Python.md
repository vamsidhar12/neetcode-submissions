# DSA Complete Interview Playbook — All Patterns

---

## 🧠 UNIVERSAL PROBLEM CLASSIFIER

> Before writing a single line, ask these questions **in order**:

```
1.  Is the input SORTED or can I sort it?        → Binary Search or Two Pointer
2.  Do I need a SUBARRAY or SUBSTRING?           → Sliding Window
3.  Do I need ALL combinations / permutations?   → Backtracking
4.  Is it a TREE?                                → DFS/BFS tree patterns
5.  Is it a GRAPH (explicit or implicit)?        → BFS/DFS/Dijkstra/Union-Find
6.  Does it ask OPTIMAL / COUNT / MIN-MAX?       → DP
7.  Does it involve INTERVALS?                   → Interval patterns
8.  Does it involve a STREAM or TOP-K?           → Heap
9.  Does it involve FAST LOOKUPS?                → HashMap / HashSet
10. Does it need O(1) space with sorted input?   → Two Pointer
```

---

## 1. TWO POINTERS

**Mental Model**
Two pointers work when the array is sorted (or can be sorted) and moving pointers inward gives you monotonic information — moving left pointer right increases sum, moving right pointer left decreases it.

**Three flavors:**
- Opposite ends → converge toward middle (pair sum, container with most water)
- Same direction → fast/slow (cycle detection, remove duplicates)
- Two arrays → merge two sorted arrays

---

### Pattern 1.1 — Opposite End (Pair Sum / Target)
**Trigger:** sorted array, find pair/triplet summing to target

```python
lo, hi = 0, len(nums) - 1
while lo < hi:
    s = nums[lo] + nums[hi]
    if s == target:
        # record result
        lo += 1; hi -= 1
    elif s < target:
        lo += 1
    else:
        hi -= 1
```
**LC:** 167 Two Sum II, 15 3Sum, 16 3Sum Closest, 11 Container With Most Water

---

### Pattern 1.2 — Fast/Slow Pointer
**Trigger:** cycle detection, find middle of linked list, remove Nth from end

```python
# Cycle detection (Floyd's)
slow = fast = head
while fast and fast.next:
    slow = slow.next
    fast = fast.next.next
    if slow is fast:
        return True  # cycle

# Find middle
slow = fast = head
while fast and fast.next:
    slow = slow.next
    fast = fast.next.next
# slow = middle
```
**LC:** 141 Linked List Cycle, 142 Cycle Start, 876 Middle of Linked List, 19 Remove Nth From End

---

### Pattern 1.3 — Remove Duplicates In-Place
**Trigger:** sorted array, modify in-place, return new length

```python
write = 1
for read in range(1, len(nums)):
    if nums[read] != nums[write - 1]:
        nums[write] = nums[read]
        write += 1
return write
```
**LC:** 26 Remove Duplicates, 80 Remove Duplicates II, 27 Remove Element

---

### Pattern 1.4 — Dutch National Flag (3-Way Partition)
**Trigger:** sort array with 3 distinct values in-place O(N) O(1)

```python
lo, mid, hi = 0, 0, len(nums) - 1
while mid <= hi:
    if nums[mid] == 0:
        nums[lo], nums[mid] = nums[mid], nums[lo]
        lo += 1; mid += 1
    elif nums[mid] == 1:
        mid += 1
    else:
        nums[mid], nums[hi] = nums[hi], nums[mid]
        hi -= 1
```
**LC:** 75 Sort Colors

---

## 2. SLIDING WINDOW

**Mental Model**
A window `[lo, hi]` over a sequence. Expand `hi` to grow the window, shrink `lo` to restore a constraint. The window always represents the current candidate answer.

**Two subtypes:**
- Fixed size window — `hi - lo + 1 == K` always
- Variable size window — expand until invalid, shrink until valid again

---

### Pattern 2.1 — Fixed Size Window
**Trigger:** "subarray of size K", "average of K consecutive"

```python
window_sum = sum(nums[:k])
max_sum = window_sum
for i in range(k, len(nums)):
    window_sum += nums[i] - nums[i - k]  # slide: add new, remove old
    max_sum = max(max_sum, window_sum)
```
**LC:** 643 Maximum Average Subarray, 1343 Product of Arrays Less Than K, 567 Permutation in String

---

### Pattern 2.2 — Variable Size Window (Longest Valid)
**Trigger:** "longest subarray/substring with condition X"

```python
from collections import defaultdict
freq = defaultdict(int)
lo = 0
max_len = 0
for hi in range(len(s)):
    freq[s[hi]] += 1                        # expand
    while invalid(freq):                    # shrink until valid
        freq[s[lo]] -= 1
        if freq[s[lo]] == 0:
            del freq[s[lo]]
        lo += 1
    max_len = max(max_len, hi - lo + 1)
```
**LC:** 3 Longest Substring Without Repeating, 76 Minimum Window Substring, 424 Longest Repeating Character Replacement, 1004 Max Consecutive Ones III

---

### Pattern 2.3 — Variable Size Window (Shortest Valid)
**Trigger:** "smallest subarray with sum ≥ target"

```python
lo = 0
total = 0
min_len = float('inf')
for hi in range(len(nums)):
    total += nums[hi]
    while total >= target:
        min_len = min(min_len, hi - lo + 1)
        total -= nums[lo]
        lo += 1
```
**LC:** 209 Minimum Size Subarray Sum, 76 Minimum Window Substring

---

### Pattern 2.4 — At Most K Trick
**Trigger:** "exactly K distinct" → solve as `atMost(K) - atMost(K-1)`

```python
def at_most(nums, k):
    freq = defaultdict(int)
    lo = 0
    result = 0
    for hi in range(len(nums)):
        if freq[nums[hi]] == 0:
            k -= 1
        freq[nums[hi]] += 1
        while k < 0:
            freq[nums[lo]] -= 1
            if freq[nums[lo]] == 0:
                k += 1
            lo += 1
        result += hi - lo + 1
    return result

# exactly_k = at_most(k) - at_most(k - 1)
```
**LC:** 992 Subarrays with K Different Integers, 930 Binary Subarrays With Sum

---

## 3. BINARY SEARCH

**Mental Model**
Binary search works on any **monotonic property** — not just sorted arrays. If you can define a predicate `P(x)` such that all answers below a threshold are false and all above are true (or vice versa), you can binary search.

**Universal template (find leftmost valid):**

```python
lo, hi = min_val, max_val
while lo < hi:
    mid = lo + (hi - lo) // 2
    if feasible(mid):
        hi = mid       # mid could be answer, keep it
    else:
        lo = mid + 1
return lo  # lo == hi == answer
```

> **Find rightmost valid:** replace `hi = mid` with `lo = mid` and `lo = mid + 1` with `hi = mid - 1`. Use `mid = lo + (hi - lo + 1) // 2` to avoid infinite loop.

---

### Pattern 3.1 — Classic Binary Search
**Trigger:** sorted array, find exact value or insert position

```python
lo, hi = 0, len(nums) - 1
while lo <= hi:
    mid = lo + (hi - lo) // 2
    if nums[mid] == target:
        return mid
    elif nums[mid] < target:
        lo = mid + 1
    else:
        hi = mid - 1
return -1  # not found
```
**LC:** 704 Binary Search, 35 Search Insert Position

---

### Pattern 3.2 — Find Leftmost / Rightmost Occurrence

```python
# Leftmost occurrence
lo, hi = 0, len(nums)
while lo < hi:
    mid = lo + (hi - lo) // 2
    if nums[mid] < target:
        lo = mid + 1
    else:
        hi = mid
return lo  # first position >= target

# Rightmost occurrence
lo, hi = 0, len(nums)
while lo < hi:
    mid = lo + (hi - lo + 1) // 2
    if nums[mid] > target:
        hi = mid - 1
    else:
        lo = mid
return lo  # last position <= target
```
**LC:** 34 Find First and Last Position, 278 First Bad Version, 374 Guess Number

---

### Pattern 3.3 — Binary Search on Answer ("Minimize Maximum")
**Trigger:** "minimum maximum", "maximum minimum", "can we do it in K operations"

```python
def feasible(mid):
    # check if mid is achievable
    pass

lo, hi = min_possible, max_possible
while lo < hi:
    mid = lo + (hi - lo) // 2
    if feasible(mid):
        hi = mid
    else:
        lo = mid + 1
return lo
```
**LC:** 410 Split Array Largest Sum, 875 Koko Eating Bananas, 1011 Capacity to Ship Packages, 1482 Minimum Days to Make Bouquets

---

### Pattern 3.4 — Binary Search in Rotated Array
**Trigger:** sorted array rotated at unknown pivot

```python
lo, hi = 0, len(nums) - 1
while lo <= hi:
    mid = lo + (hi - lo) // 2
    if nums[mid] == target:
        return mid
    if nums[lo] <= nums[mid]:           # left half sorted
        if nums[lo] <= target < nums[mid]:
            hi = mid - 1
        else:
            lo = mid + 1
    else:                               # right half sorted
        if nums[mid] < target <= nums[hi]:
            lo = mid + 1
        else:
            hi = mid - 1
return -1
```
**LC:** 33 Search in Rotated Sorted Array, 81 with duplicates, 153 Find Minimum in Rotated Array

---

### Pattern 3.5 — Binary Search on Matrix
**Trigger:** sorted matrix where each row is sorted, first element > last row's last

```python
# Treat m×n matrix as 1D array of size m*n
m, n = len(matrix), len(matrix[0])
lo, hi = 0, m * n - 1
while lo <= hi:
    mid = lo + (hi - lo) // 2
    val = matrix[mid // n][mid % n]
    if val == target:
        return True
    elif val < target:
        lo = mid + 1
    else:
        hi = mid - 1
return False
```
**LC:** 74 Search a 2D Matrix, 240 Search a 2D Matrix II (staircase search)

---

## 4. SORTING

### Pattern 4.1 — Custom Comparator Sort

```python
import functools

# Sort by primary key x, then secondary key y
items.sort(key=lambda a: (a.x, a.y))

# For complex comparisons, use functools.cmp_to_key
def compare(a, b):
    if a.x != b.x:
        return -1 if a.x < b.x else 1
    return -1 if a.y < b.y else (1 if a.y > b.y else 0)

items.sort(key=functools.cmp_to_key(compare))
```
**LC:** 56 Merge Intervals, 435 Non-Overlapping Intervals, 973 K Closest Points

---

### Pattern 4.2 — Top-K with Heap

```python
import heapq

# K largest — use min-heap of size K
heap = []
for x in nums:
    heapq.heappush(heap, x)
    if len(heap) > k:
        heapq.heappop(heap)
# heap now contains the K largest elements
```
> Complexity: O(N log K). Use `heapq.nlargest(k, nums)` for O(N) if order within K doesn't matter.

**LC:** 215, 347, 973

---

### Pattern 4.3 — Counting / Bucket Sort

```python
count = [0] * (K + 1)
for x in nums:
    count[x] += 1
result = []
for i in range(K + 1):
    result.extend([i] * count[i])
```
**LC:** 347 Top K Frequent (bucket by frequency index), 75 Sort Colors

---

## 5. RECURSION & BACKTRACKING

### Pattern 5.1 — Subsets (Include / Exclude)

```python
def backtrack(i, cur):
    result.append(cur[:])
    for j in range(i, len(nums)):
        if j > i and nums[j] == nums[j - 1]:
            continue  # skip duplicates
        cur.append(nums[j])
        backtrack(j + 1, cur)
        cur.pop()

result = []
nums.sort()
backtrack(0, [])
```
**LC:** 78 Subsets, 90 Subsets II, 77 Combinations, 39/40 Combination Sum

---

### Pattern 5.2 — Permutations

```python
def backtrack(used, cur):
    if len(cur) == len(nums):
        result.append(cur[:])
        return
    for i in range(len(nums)):
        if used[i]:
            continue
        if i > 0 and nums[i] == nums[i - 1] and not used[i - 1]:
            continue
        used[i] = True
        cur.append(nums[i])
        backtrack(used, cur)
        cur.pop()
        used[i] = False

result = []
nums.sort()
backtrack([False] * len(nums), [])
```
**LC:** 46 Permutations, 47 Permutations II

---

### Pattern 5.3 — General Backtracking (Constraint Satisfaction)

```python
def backtrack(state):
    if is_invalid(state):
        return           # prune early
    if is_complete(state):
        record(state)
        return
    for choice in get_choices(state):
        apply(choice, state)
        backtrack(state)
        undo(choice, state)  # always undo
```
> **Pruning is everything. Prune as early as possible.**

**LC:** 51 N-Queens, 37 Sudoku Solver, 79 Word Search, 131 Palindrome Partitioning

---

## 6. TREES

### Pattern 6.1 — DFS (Pre/In/Post Order)

```python
def dfs(node):
    if not node:
        return
    # PREORDER here
    dfs(node.left)
    # INORDER here — BST inorder = sorted!
    dfs(node.right)
    # POSTORDER here
```

---

### Pattern 6.2 — BFS / Level Order

```python
from collections import deque

q = deque([root])
while q:
    for _ in range(len(q)):
        node = q.popleft()
        if node.left:
            q.append(node.left)
        if node.right:
            q.append(node.right)
```
**LC:** 102, 199 Right Side View, 111 Min Depth, 103 Zigzag Traversal

---

### Pattern 6.3 — Bottom-Up (Return Value to Parent)

```python
global_ans = 0

def dfs(node):
    global global_ans
    if not node:
        return 0
    L = dfs(node.left)
    R = dfs(node.right)
    global_ans = max(global_ans, L + R + node.val)  # update global
    return 1 + max(L, R)                             # return to parent
```
> **Note:** global answer and return value are usually **different things**.

**LC:** 104, 543 Diameter, 124 Max Path Sum, 110 Balanced, 968 Camera Coverage

---

### Pattern 6.4 — Top-Down (Pass Info to Children)

```python
def validate(node, lo=float('-inf'), hi=float('inf')):
    if not node:
        return True
    if not (lo < node.val < hi):
        return False
    return (validate(node.left, lo, node.val) and
            validate(node.right, node.val, hi))
```
**LC:** 98 Validate BST, 112 Path Sum, 257 Binary Tree Paths

---

### Pattern 6.5 — LCA

```python
def lca(root, p, q):
    if not root or root is p or root is q:
        return root
    L = lca(root.left, p, q)
    R = lca(root.right, p, q)
    return root if (L and R) else (L or R)
```
**LC:** 236, 235 BST LCA

---

### Pattern 6.6 — Trie

```python
class Trie:
    def __init__(self):
        self.children = {}
        self.is_end = False

    def insert(self, word):
        node = self
        for ch in word:
            if ch not in node.children:
                node.children[ch] = Trie()
            node = node.children[ch]
        node.is_end = True
```
**LC:** 208, 212 Word Search II, 211 Design Add/Search Words

---

## 7. GRAPHS

### Pattern 7.1 — BFS (Unweighted Shortest Path)

```python
from collections import deque

visited = [False] * n
q = deque([src])
visited[src] = True
dist = 0
while q:
    for _ in range(len(q)):
        u = q.popleft()
        if u == dst:
            return dist
        for v in adj[u]:
            if not visited[v]:
                visited[v] = True
                q.append(v)
    dist += 1
```
**LC:** 127 Word Ladder, 994 Rotting Oranges, 286 Walls and Gates

---

### Pattern 7.2 — DFS / Connected Components

```python
def dfs(u):
    visited[u] = True
    for v in adj[u]:
        if not visited[v]:
            dfs(v)

visited = [False] * n
components = 0
for i in range(n):
    if not visited[i]:
        dfs(i)
        components += 1
```
**LC:** 200 Number of Islands, 130 Surrounded Regions, 323 Number of Connected Components

---

### Pattern 7.3 — Union-Find

```python
class UnionFind:
    def __init__(self, n):
        self.parent = list(range(n))
        self.rank = [0] * n

    def find(self, x):
        if self.parent[x] != x:
            self.parent[x] = self.find(self.parent[x])  # path compression
        return self.parent[x]

    def unite(self, x, y):
        x, y = self.find(x), self.find(y)
        if x == y:
            return False
        if self.rank[x] < self.rank[y]:
            x, y = y, x
        self.parent[y] = x
        if self.rank[x] == self.rank[y]:
            self.rank[x] += 1
        return True
```
**LC:** 684 Redundant Connection, 721 Accounts Merge, 1584 Min Cost Connect Points

---

### Pattern 7.4 — Topological Sort (Kahn's)

```python
from collections import deque

indeg = [0] * n
adj = [[] for _ in range(n)]
for u, v in edges:
    adj[u].append(v)
    indeg[v] += 1

q = deque(i for i in range(n) if indeg[i] == 0)
order = []
while q:
    u = q.popleft()
    order.append(u)
    for v in adj[u]:
        indeg[v] -= 1
        if indeg[v] == 0:
            q.append(v)

return len(order) == n  # False = cycle
```
**LC:** 207 Course Schedule, 210 Course Schedule II, 269 Alien Dictionary

---

### Pattern 7.5 — Dijkstra

```python
import heapq

dist = [float('inf')] * n
dist[src] = 0
pq = [(0, src)]
while pq:
    d, u = heapq.heappop(pq)
    if d > dist[u]:
        continue
    for v, w in adj[u]:
        if dist[u] + w < dist[v]:
            dist[v] = dist[u] + w
            heapq.heappush(pq, (dist[v], v))
```
**LC:** 743 Network Delay Time, 787 Cheapest Flights K Stops, 1631 Min Effort Path

---

### Pattern 7.6 — Cycle Detection (Directed — 3 Colors)

```python
# 0 = unvisited, 1 = in-stack, 2 = done
def dfs(u):
    color[u] = 1
    for v in adj[u]:
        if color[v] == 1:
            return True   # back edge = cycle
        if color[v] == 0 and dfs(v):
            return True
    color[u] = 2
    return False
```

---

### Pattern 7.7 — Multi-Source BFS

> Push **all sources at step 0**. BFS naturally finds minimum distance from any source simultaneously.

**LC:** 994 Rotting Oranges, 286 Walls and Gates, 1162 As Far From Land As Possible

---

## 8. DYNAMIC PROGRAMMING

### Pattern 8.1 — 1D Linear DP

```python
dp = [0] * n
dp[0] = base
for i in range(1, n):
    dp[i] = max(dp[i - 1] + nums[i], nums[i])  # example: max subarray
```
**LC:** 70 Climbing Stairs, 198 House Robber, 53 Max Subarray (Kadane's), 300 LIS

---

### Pattern 8.2 — Kadane's

```python
cur = best = nums[0]
for i in range(1, len(nums)):
    cur = max(nums[i], cur + nums[i])
    best = max(best, cur)
```
**LC:** 53, 152 Max Product Subarray (track min too)

---

### Pattern 8.3 — 2D DP (Grid / Two Sequences)

```python
dp = [[0] * (n + 1) for _ in range(m + 1)]
for i in range(1, m + 1):
    for j in range(1, n + 1):
        if A[i - 1] == B[j - 1]:
            dp[i][j] = dp[i - 1][j - 1] + 1
        else:
            dp[i][j] = max(dp[i - 1][j], dp[i][j - 1])
```
**LC:** 62 Unique Paths, 64 Min Path Sum, 1143 LCS, 72 Edit Distance

---

### Pattern 8.4 — 0/1 Knapsack

```python
dp = [0] * (W + 1)
for w, v in items:
    for c in range(W, w - 1, -1):  # BACKWARDS = each item once
        dp[c] = max(dp[c], dp[c - w] + v)
```
**LC:** 416 Partition Equal Subset Sum, 494 Target Sum

---

### Pattern 8.5 — Unbounded Knapsack (Coin Change)

```python
dp = [float('inf')] * (amount + 1)
dp[0] = 0
for coin in coins:
    for c in range(coin, amount + 1):  # FORWARDS = reuse allowed
        if dp[c - coin] != float('inf'):
            dp[c] = min(dp[c], dp[c - coin] + 1)
```
**LC:** 322 Coin Change, 518 Coin Change II

---

### Pattern 8.6 — Interval DP

```python
dp = [[0] * n for _ in range(n)]
for length in range(2, n + 1):
    for i in range(n - length + 1):
        j = i + length - 1
        for k in range(i, j):
            dp[i][j] = max(dp[i][j], dp[i][k] + dp[k + 1][j] + cost)
```
**LC:** 312 Burst Balloons, 516 Longest Palindromic Subsequence

---

### Pattern 8.7 — State Machine DP (Stocks)

```python
hold = float('-inf')
sold = rest = 0
for p in prices:
    prev_sold = sold
    sold = hold + p
    hold = max(hold, rest - p)
    rest = max(rest, prev_sold)
return max(sold, rest)
```
**LC:** 121 / 122 / 123 / 188 / 309 / 714 (all stock variants)

---

### Pattern 8.8 — Memoized DFS (Top-Down)

```python
from functools import lru_cache

@lru_cache(maxsize=None)
def dp(i):
    if i >= n:
        return 0
    return max(dp(i + 1), nums[i] + dp(i + 2))
```
**LC:** 198 House Robber, 139 Word Break, 329 LIP in Matrix

---

### Pattern 8.9 — Bitmask DP

```python
INF = float('inf')
dp = [INF] * (1 << n)
dp[0] = 0
for mask in range(1 << n):
    pos = bin(mask).count('1')
    for j in range(n):
        if mask >> j & 1:
            continue
        next_mask = mask | (1 << j)
        dp[next_mask] = min(dp[next_mask], dp[mask] + cost[pos][j])
return dp[(1 << n) - 1]
```
**LC:** 847 Shortest Path Visiting All Nodes, 943 Find Shortest Superstring

---

## 9. INTERVALS

**Mental Model**
Always sort by **start time** first. Then iterate and decide: does the current interval overlap the previous one?

```
Overlap condition: cur.start <= prev.end
```

---

### Pattern 9.1 — Merge Intervals

```python
intervals.sort(key=lambda x: x[0])
res = [intervals[0]]
for iv in intervals:
    if iv[0] <= res[-1][1]:
        res[-1][1] = max(res[-1][1], iv[1])  # merge
    else:
        res.append(iv)
```
**LC:** 56 Merge Intervals, 57 Insert Interval

---

### Pattern 9.2 — Non-Overlapping (Greedy Removal)
**Trigger:** minimum removals to make non-overlapping

```python
intervals.sort(key=lambda x: x[1])  # sort by END time — greedy keep earliest-ending
count = 0
end = float('-inf')
for iv in intervals:
    if iv[0] >= end:
        end = iv[1]   # no overlap, keep
    else:
        count += 1    # overlap, remove current
return count
```
**LC:** 435 Non-Overlapping Intervals, 452 Minimum Arrows to Burst Balloons

---

### Pattern 9.3 — Meeting Rooms (Simultaneous Intervals)

```python
import heapq

intervals.sort()
ends = []  # min-heap of end times
for iv in intervals:
    if ends and ends[0] <= iv[0]:
        heapq.heapreplace(ends, iv[1])  # free a room
    else:
        heapq.heappush(ends, iv[1])
return len(ends)  # rooms needed
```
**LC:** 253 Meeting Rooms II, 252 Meeting Rooms I

---

## 10. LINKED LIST

### Pattern 10.1 — Reverse Linked List

```python
prev, cur = None, head
while cur:
    nxt = cur.next
    cur.next = prev
    prev = cur
    cur = nxt
return prev
```
**LC:** 206, 92 Reverse Between (partial), 25 Reverse K Group

---

### Pattern 10.2 — Find Cycle Start

```python
slow = fast = head
while fast and fast.next:
    slow = slow.next
    fast = fast.next.next
    if slow is fast:
        slow = head
        while slow is not fast:
            slow = slow.next
            fast = fast.next
        return slow  # cycle start
return None
```
**LC:** 142 Linked List Cycle II

---

### Pattern 10.3 — Merge Two Sorted Lists

```python
class ListNode:
    def __init__(self, val=0, next=None):
        self.val = val; self.next = next

dummy = ListNode(0)
cur = dummy
while l1 and l2:
    if l1.val <= l2.val:
        cur.next = l1; l1 = l1.next
    else:
        cur.next = l2; l2 = l2.next
    cur = cur.next
cur.next = l1 if l1 else l2
return dummy.next
```
**LC:** 21, 23 Merge K Sorted (use min-heap)

---

## 11. STACK & MONOTONIC STACK

**Mental Model**
A monotonic stack maintains elements in increasing or decreasing order. When you push a new element that violates the order, you pop until the order is restored — **each pop is your "answer" for the popped element**.

---

### Pattern 11.1 — Next Greater Element

```python
st = []
res = [-1] * len(nums)
for i in range(len(nums)):
    while st and nums[st[-1]] < nums[i]:
        res[st[-1]] = nums[i]  # i is the next greater for st[-1]
        st.pop()
    st.append(i)
```
**LC:** 496 Next Greater Element, 739 Daily Temperatures, 503 Circular Next Greater

---

### Pattern 11.2 — Largest Rectangle in Histogram

```python
st = []
max_area = 0
nums = nums + [0]  # sentinel
for i in range(len(nums)):
    while st and nums[st[-1]] > nums[i]:
        h = nums[st.pop()]
        w = i if not st else i - st[-1] - 1
        max_area = max(max_area, h * w)
    st.append(i)
```
**LC:** 84 Largest Rectangle in Histogram, 85 Maximal Rectangle

---

### Pattern 11.3 — Valid Parentheses / Matching

```python
st = []
pairs = {')': '(', ']': '[', '}': '{'}
for c in s:
    if c in '([{':
        st.append(c)
    else:
        if not st or st[-1] != pairs[c]:
            return False
        st.pop()
return len(st) == 0
```
**LC:** 20 Valid Parentheses, 394 Decode String, 1249 Minimum Remove to Make Valid

---

## 12. HEAP / PRIORITY QUEUE

### Pattern 12.1 — Two Heaps (Median of Stream)

```python
import heapq

lo = []  # max-heap (negate values): lower half
hi = []  # min-heap: upper half

def add_num(num):
    heapq.heappush(lo, -num)
    heapq.heappush(hi, -heapq.heappop(lo))   # send max of lo to hi
    if len(hi) > len(lo):
        heapq.heappush(lo, -heapq.heappop(hi))  # keep lo.size() >= hi.size()

def find_median():
    if len(lo) > len(hi):
        return -lo[0]
    return (-lo[0] + hi[0]) / 2.0
```
**LC:** 295 Find Median from Data Stream, 480 Sliding Window Median

---

### Pattern 12.2 — Merge K Sorted Lists / K-Way Merge

```python
import heapq

pq = []
for i, node in enumerate(lists):
    if node:
        heapq.heappush(pq, (node.val, i, node))

dummy = ListNode(0)
cur = dummy
while pq:
    val, i, node = heapq.heappop(pq)
    cur.next = node
    cur = node
    if node.next:
        heapq.heappush(pq, (node.next.val, i, node.next))
return dummy.next
```
**LC:** 23 Merge K Sorted Lists, 378 Kth Smallest in Sorted Matrix

---

## 13. HASHING

### Pattern 13.1 — Two Sum (HashMap)

```python
seen = {}
for i, num in enumerate(nums):
    comp = target - num
    if comp in seen:
        return [seen[comp], i]
    seen[num] = i
```
**LC:** 1 Two Sum, 167 Two Sum II, 170 Two Sum III

---

### Pattern 13.2 — Frequency Map

```python
from collections import Counter

freq = Counter(nums)
```
**LC:** 347 Top K Frequent, 242 Valid Anagram, 49 Group Anagrams

---

### Pattern 13.3 — Prefix Sum + HashMap
**Trigger:** "subarray sum equals K", "number of subarrays with sum divisible by K"

```python
from collections import defaultdict

count = defaultdict(int)
count[0] = 1
total = 0
result = 0
for x in nums:
    total += x
    result += count[total - k]  # if total-k seen before, subarray exists
    count[total] += 1
```
**LC:** 560 Subarray Sum Equals K, 974 Subarray Sums Divisible by K, 525 Contiguous Array

---

## ⚡ COMPLEXITY QUICK REFERENCE

| Pattern | Time | Space |
|---|---|---|
| Two Pointer | O(N) | O(1) |
| Sliding Window | O(N) | O(K) |
| Binary Search | O(log N) | O(1) |
| Binary Search on Answer | O(N log K) | O(1) |
| Sorting | O(N log N) | O(log N) |
| Counting Sort | O(N + K) | O(K) |
| Heap top-K | O(N log K) | O(K) |
| heapq.nlargest | O(N) avg | O(K) |
| Backtracking | O(branches^depth) | O(depth) |
| BFS / DFS | O(V + E) | O(V) |
| Dijkstra | O((V+E) log V) | O(V) |
| Union-Find | O(α(N)) ≈ O(1) | O(N) |
| Topo Sort | O(V + E) | O(V) |
| 1D DP | O(N) | O(1)–O(N) |
| 2D DP | O(M×N) | O(M×N) |
| Knapsack | O(N×W) | O(W) |
| Interval DP | O(N³) | O(N²) |
| Bitmask DP | O(2ᴺ×N) | O(2ᴺ) |
| Monotonic Stack | O(N) | O(N) |
| Prefix Sum + HashMap | O(N) | O(N) |
| Two Heaps | O(log N) per op | O(N) |

---

## 🌳 THE COMPLETE DECISION TREE

```
SEE THE PROBLEM
     │
     ├─ Input is SORTED / can be sorted?
     │       ├─ Find pair/triplet summing to target   → Two Pointer (opposite ends)
     │       ├─ Find element / insert position        → Binary Search
     │       └─ Minimize MAX / maximize MIN           → Binary Search on Answer
     │
     ├─ SUBARRAY or SUBSTRING?
     │       ├─ Fixed size K                          → Fixed Sliding Window
     │       ├─ Longest with condition                → Variable Window (expand/shrink)
     │       ├─ Shortest with condition               → Variable Window (shrink fast)
     │       └─ Sum equals K                          → Prefix Sum + HashMap
     │
     ├─ LINKED LIST?
     │       ├─ Cycle?                                → Fast/Slow Pointer
     │       ├─ Reverse?                              → Iterative reverse
     │       └─ Merge sorted?                         → Dummy node + compare
     │
     ├─ GENERATE ALL / ENUMERATE?
     │       ├─ Subsets                               → Backtrack include/exclude
     │       ├─ Permutations                          → Backtrack with used[]
     │       └─ Constraint satisfaction               → Backtrack + prune early
     │
     ├─ TREE?
     │       ├─ Need subtree info                     → Bottom-up DFS
     │       ├─ Need ancestor info                    → Top-down DFS
     │       ├─ Level / min depth                     → BFS
     │       ├─ BST? Inorder = sorted                 → Inorder traversal
     │       ├─ LCA                                   → Return-based postorder
     │       └─ Prefix / word search                  → Trie
     │
     ├─ GRAPH?
     │       ├─ Unweighted shortest path              → BFS
     │       ├─ Weighted shortest path                → Dijkstra (min-heap)
     │       ├─ Connected components                  → Union-Find or DFS
     │       ├─ Dependencies / detect cycle           → Topological Sort
     │       └─ Minimum spanning tree                 → Kruskal (Union-Find)
     │
     ├─ INTERVALS?
     │       ├─ Merge overlapping                     → Sort by start, scan
     │       ├─ Min removals                          → Sort by end, greedy
     │       └─ Max simultaneous                      → Min-heap of end times
     │
     ├─ STACK?
     │       ├─ Next greater / smaller                → Monotonic Stack
     │       ├─ Largest rectangle                     → Monotonic Stack
     │       └─ Matching brackets                     → Simple Stack
     │
     ├─ TOP-K / STREAM?
     │       ├─ K largest / smallest                  → Heap size K
     │       ├─ Median of stream                      → Two Heaps
     │       └─ Merge K sorted                        → Min-heap K-way merge
     │
     └─ OPTIMAL / COUNT / MIN-MAX?                    → DP
             ├─ 1D sequence                           → Linear DP
             ├─ Two sequences / grid                  → 2D DP table
             ├─ Pick items, budget constraint         → Knapsack (back=0/1, fwd=unbounded)
             ├─ Merge intervals optimally             → Interval DP
             ├─ Buy/sell with states                  → State machine DP
             ├─ Sparse / irregular states             → Memoized DFS + lru_cache
             └─ N ≤ 20, subsets                       → Bitmask DP
```

---

## 🏆 THE FIVE INTERVIEW RULES

1. **State complexity before coding** — shows you understand what you're building
2. **Name the pattern out loud** — "this is a sliding window" signals confidence
3. **Edge cases first** — empty, single element, all same, negative numbers, overflow
4. **Walk one example after coding** — catch off-by-ones before the interviewer does
