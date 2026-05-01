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

```java
int lo = 0, hi = n - 1;
while (lo < hi) {
    int sum = nums[lo] + nums[hi];
    if (sum == target)      { record(); lo++; hi--; }
    else if (sum < target)  lo++;
    else                    hi--;
}
```
**LC:** 167 Two Sum II, 15 3Sum, 16 3Sum Closest, 11 Container With Most Water

---

### Pattern 1.2 — Fast/Slow Pointer
**Trigger:** cycle detection, find middle of linked list, remove Nth from end

```java
// Cycle detection (Floyd's)
ListNode slow = head, fast = head;
while (fast != null && fast.next != null) {
    slow = slow.next;
    fast = fast.next.next;
    if (slow == fast) return true; // cycle
}

// Find middle
while (fast != null && fast.next != null) {
    slow = slow.next;
    fast = fast.next.next;
}
// slow = middle
```
**LC:** 141 Linked List Cycle, 142 Cycle Start, 876 Middle of Linked List, 19 Remove Nth From End

---

### Pattern 1.3 — Remove Duplicates In-Place
**Trigger:** sorted array, modify in-place, return new length

```java
int write = 1;
for (int read = 1; read < n; read++) {
    if (nums[read] != nums[write - 1])
        nums[write++] = nums[read];
}
return write;
```
**LC:** 26 Remove Duplicates, 80 Remove Duplicates II, 27 Remove Element

---

### Pattern 1.4 — Dutch National Flag (3-Way Partition)
**Trigger:** sort array with 3 distinct values in-place O(N) O(1)

```java
int lo = 0, mid = 0, hi = n - 1;
while (mid <= hi) {
    if (nums[mid] == 0) {
        int tmp = nums[lo]; nums[lo] = nums[mid]; nums[mid] = tmp;
        lo++; mid++;
    } else if (nums[mid] == 1) {
        mid++;
    } else {
        int tmp = nums[mid]; nums[mid] = nums[hi]; nums[hi] = tmp;
        hi--;
    }
}
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

```java
int windowSum = 0;
for (int i = 0; i < k; i++) windowSum += nums[i];
int maxSum = windowSum;
for (int i = k; i < n; i++) {
    windowSum += nums[i] - nums[i - k]; // slide: add new, remove old
    maxSum = Math.max(maxSum, windowSum);
}
```
**LC:** 643 Maximum Average Subarray, 1343 Product of Arrays Less Than K, 567 Permutation in String

---

### Pattern 2.2 — Variable Size Window (Longest Valid)
**Trigger:** "longest subarray/substring with condition X"

```java
Map<Character, Integer> freq = new HashMap<>();
int lo = 0, maxLen = 0;
for (int hi = 0; hi < s.length(); hi++) {
    freq.merge(s.charAt(hi), 1, Integer::sum);          // expand
    while (invalid(freq)) {                              // shrink until valid
        char c = s.charAt(lo);
        freq.put(c, freq.get(c) - 1);
        if (freq.get(c) == 0) freq.remove(c);
        lo++;
    }
    maxLen = Math.max(maxLen, hi - lo + 1);
}
```
**LC:** 3 Longest Substring Without Repeating, 76 Minimum Window Substring, 424 Longest Repeating Character Replacement, 1004 Max Consecutive Ones III

---

### Pattern 2.3 — Variable Size Window (Shortest Valid)
**Trigger:** "smallest subarray with sum ≥ target"

```java
int lo = 0, sum = 0, minLen = Integer.MAX_VALUE;
for (int hi = 0; hi < n; hi++) {
    sum += nums[hi];
    while (sum >= target) {
        minLen = Math.min(minLen, hi - lo + 1);
        sum -= nums[lo++];
    }
}
```
**LC:** 209 Minimum Size Subarray Sum, 76 Minimum Window Substring

---

### Pattern 2.4 — At Most K Trick
**Trigger:** "exactly K distinct" → solve as `atMost(K) - atMost(K-1)`

```java
int atMost(int[] nums, int k) {
    Map<Integer, Integer> freq = new HashMap<>();
    int lo = 0, result = 0;
    for (int hi = 0; hi < nums.length; hi++) {
        if (freq.merge(nums[hi], 1, Integer::sum) == 1) k--;
        while (k < 0) {
            int cnt = freq.merge(nums[lo], -1, Integer::sum);
            if (cnt == 0) { freq.remove(nums[lo]); k++; }
            lo++;
        }
        result += hi - lo + 1;
    }
    return result;
}
// exactlyK = atMost(k) - atMost(k-1)
```
**LC:** 992 Subarrays with K Different Integers, 930 Binary Subarrays With Sum

---

## 3. BINARY SEARCH

**Mental Model**
Binary search works on any **monotonic property** — not just sorted arrays. If you can define a predicate `P(x)` such that all answers below a threshold are false and all above are true (or vice versa), you can binary search.

**Universal template (find leftmost valid):**

```java
int lo = minVal, hi = maxVal;
while (lo < hi) {
    int mid = lo + (hi - lo) / 2;  // avoid overflow
    if (feasible(mid)) hi = mid;   // mid could be answer, keep it
    else               lo = mid + 1;
}
return lo; // lo == hi = answer
```

> **Find rightmost valid:** replace `hi = mid` with `lo = mid` and `lo = mid + 1` with `hi = mid - 1`. Use `mid = lo + (hi - lo + 1) / 2` to avoid infinite loop.

---

### Pattern 3.1 — Classic Binary Search
**Trigger:** sorted array, find exact value or insert position

```java
int lo = 0, hi = n - 1;
while (lo <= hi) {
    int mid = lo + (hi - lo) / 2;
    if (nums[mid] == target) return mid;
    else if (nums[mid] < target) lo = mid + 1;
    else                         hi = mid - 1;
}
return -1; // not found
```
**LC:** 704 Binary Search, 35 Search Insert Position

---

### Pattern 3.2 — Find Leftmost / Rightmost Occurrence

```java
// Leftmost occurrence
int lo = 0, hi = n;
while (lo < hi) {
    int mid = lo + (hi - lo) / 2;
    if (nums[mid] < target) lo = mid + 1;
    else                    hi = mid;
}
return lo; // first position >= target

// Rightmost occurrence
lo = 0; hi = n;
while (lo < hi) {
    int mid = lo + (hi - lo + 1) / 2;
    if (nums[mid] > target) hi = mid - 1;
    else                    lo = mid;
}
return lo; // last position <= target
```
**LC:** 34 Find First and Last Position, 278 First Bad Version, 374 Guess Number

---

### Pattern 3.3 — Binary Search on Answer ("Minimize Maximum")
**Trigger:** "minimum maximum", "maximum minimum", "can we do it in K operations"

```java
boolean feasible(int mid) {
    // check if mid is achievable
}

int lo = minPossible, hi = maxPossible;
while (lo < hi) {
    int mid = lo + (hi - lo) / 2;
    if (feasible(mid)) hi = mid;
    else               lo = mid + 1;
}
return lo;
```
**LC:** 410 Split Array Largest Sum, 875 Koko Eating Bananas, 1011 Capacity to Ship Packages, 1482 Minimum Days to Make Bouquets

---

### Pattern 3.4 — Binary Search in Rotated Array
**Trigger:** sorted array rotated at unknown pivot

```java
int lo = 0, hi = n - 1;
while (lo <= hi) {
    int mid = lo + (hi - lo) / 2;
    if (nums[mid] == target) return mid;
    if (nums[lo] <= nums[mid]) {           // left half sorted
        if (nums[lo] <= target && target < nums[mid]) hi = mid - 1;
        else                                           lo = mid + 1;
    } else {                               // right half sorted
        if (nums[mid] < target && target <= nums[hi]) lo = mid + 1;
        else                                           hi = mid - 1;
    }
}
return -1;
```
**LC:** 33 Search in Rotated Sorted Array, 81 with duplicates, 153 Find Minimum in Rotated Array

---

### Pattern 3.5 — Binary Search on Matrix
**Trigger:** sorted matrix where each row is sorted, first element > last row's last

```java
// Treat m×n matrix as 1D array of size m*n
int lo = 0, hi = m * n - 1;
while (lo <= hi) {
    int mid = lo + (hi - lo) / 2;
    int val = matrix[mid / n][mid % n];
    if (val == target) return true;
    else if (val < target) lo = mid + 1;
    else                   hi = mid - 1;
}
return false;
```
**LC:** 74 Search a 2D Matrix, 240 Search a 2D Matrix II (staircase search)

---

## 4. SORTING

### Pattern 4.1 — Custom Comparator Sort

```java
Arrays.sort(arr, (a, b) -> {
    if (a.x != b.x) return Integer.compare(a.x, b.x);
    return Integer.compare(a.y, b.y);
});
```
> **Rule:** return negative = a before b. **Never use subtraction for comparators with potential overflow.**

**LC:** 56 Merge Intervals, 435 Non-Overlapping Intervals, 973 K Closest Points

---

### Pattern 4.2 — Top-K with Heap

```java
// K largest — use min-heap of size K
PriorityQueue<Integer> pq = new PriorityQueue<>();
for (int x : nums) {
    pq.offer(x);
    if (pq.size() > k) pq.poll();
}
```
> Complexity: O(N log K). Use `nth_element` equivalent for O(N) if order within K doesn't matter.

**LC:** 215, 347, 973

---

### Pattern 4.3 — Counting / Bucket Sort

```java
int[] count = new int[K + 1];
for (int x : nums) count[x]++;
List<Integer> result = new ArrayList<>();
for (int i = 0; i <= K; i++)
    while (count[i]-- > 0) result.add(i);
```
**LC:** 347 Top K Frequent (bucket by frequency index), 75 Sort Colors

---

## 5. RECURSION & BACKTRACKING

### Pattern 5.1 — Subsets (Include / Exclude)

```java
void backtrack(int i, List<Integer> cur) {
    result.add(new ArrayList<>(cur));
    for (int j = i; j < nums.length; j++) {
        if (j > i && nums[j] == nums[j - 1]) continue; // skip dupes
        cur.add(nums[j]);
        backtrack(j + 1, cur);
        cur.remove(cur.size() - 1);
    }
}
```
**LC:** 78 Subsets, 90 Subsets II, 77 Combinations, 39/40 Combination Sum

---

### Pattern 5.2 — Permutations

```java
void backtrack(boolean[] used, List<Integer> cur) {
    if (cur.size() == nums.length) { result.add(new ArrayList<>(cur)); return; }
    for (int i = 0; i < nums.length; i++) {
        if (used[i]) continue;
        if (i > 0 && nums[i] == nums[i - 1] && !used[i - 1]) continue;
        used[i] = true;
        cur.add(nums[i]);
        backtrack(used, cur);
        cur.remove(cur.size() - 1);
        used[i] = false;
    }
}
```
**LC:** 46 Permutations, 47 Permutations II

---

### Pattern 5.3 — General Backtracking (Constraint Satisfaction)

```java
void backtrack(State s) {
    if (invalid(s)) return;        // prune early
    if (complete(s)) { record(s); return; }
    for (Choice choice : choices(s)) {
        apply(choice, s);
        backtrack(s);
        undo(choice, s);           // always undo
    }
}
```
> **Pruning is everything. Prune as early as possible.**

**LC:** 51 N-Queens, 37 Sudoku Solver, 79 Word Search, 131 Palindrome Partitioning

---

## 6. TREES

### Pattern 6.1 — DFS (Pre/In/Post Order)

```java
void dfs(TreeNode node) {
    if (node == null) return;
    // PREORDER here
    dfs(node.left);
    // INORDER here — BST inorder = sorted!
    dfs(node.right);
    // POSTORDER here
}
```

---

### Pattern 6.2 — BFS / Level Order

```java
Queue<TreeNode> q = new LinkedList<>();
q.offer(root);
while (!q.isEmpty()) {
    int sz = q.size();
    for (int i = 0; i < sz; i++) {
        TreeNode node = q.poll();
        if (node.left  != null) q.offer(node.left);
        if (node.right != null) q.offer(node.right);
    }
}
```
**LC:** 102, 199 Right Side View, 111 Min Depth, 103 Zigzag Traversal

---

### Pattern 6.3 — Bottom-Up (Return Value to Parent)

```java
int dfs(TreeNode node) {
    if (node == null) return 0;
    int L = dfs(node.left), R = dfs(node.right);
    globalAns = Math.max(globalAns, L + R + node.val); // update global
    return 1 + Math.max(L, R);                         // return to parent
}
```
> **Note:** global answer and return value are usually **different things**.

**LC:** 104, 543 Diameter, 124 Max Path Sum, 110 Balanced, 968 Camera Coverage

---

### Pattern 6.4 — Top-Down (Pass Info to Children)

```java
boolean validate(TreeNode node, long lo, long hi) {
    if (node == null) return true;
    if (node.val <= lo || node.val >= hi) return false;
    return validate(node.left, lo, node.val) &&
           validate(node.right, node.val, hi);
}
```
**LC:** 98 Validate BST, 112 Path Sum, 257 Binary Tree Paths

---

### Pattern 6.5 — LCA

```java
TreeNode lca(TreeNode root, TreeNode p, TreeNode q) {
    if (root == null || root == p || root == q) return root;
    TreeNode L = lca(root.left, p, q);
    TreeNode R = lca(root.right, p, q);
    return (L != null && R != null) ? root : (L != null ? L : R);
}
```
**LC:** 236, 235 BST LCA

---

### Pattern 6.6 — Trie

```java
class Trie {
    Trie[] ch = new Trie[26];
    boolean end = false;

    void insert(String w) {
        Trie t = this;
        for (char c : w.toCharArray()) {
            if (t.ch[c - 'a'] == null) t.ch[c - 'a'] = new Trie();
            t = t.ch[c - 'a'];
        }
        t.end = true;
    }
}
```
**LC:** 208, 212 Word Search II, 211 Design Add/Search Words

---

## 7. GRAPHS

### Pattern 7.1 — BFS (Unweighted Shortest Path)

```java
Queue<Integer> q = new LinkedList<>();
boolean[] vis = new boolean[n];
q.offer(src); vis[src] = true;
int dist = 0;
while (!q.isEmpty()) {
    int sz = q.size();
    for (int i = 0; i < sz; i++) {
        int u = q.poll();
        if (u == dst) return dist;
        for (int v : adj.get(u))
            if (!vis[v]) { vis[v] = true; q.offer(v); }
    }
    dist++;
}
return -1;
```
**LC:** 127 Word Ladder, 994 Rotting Oranges, 286 Walls and Gates

---

### Pattern 7.2 — DFS / Connected Components

```java
void dfs(int u) {
    vis[u] = true;
    for (int v : adj.get(u))
        if (!vis[v]) dfs(v);
}
int components = 0;
for (int i = 0; i < n; i++)
    if (!vis[i]) { dfs(i); components++; }
```
**LC:** 200 Number of Islands, 130 Surrounded Regions, 323 Number of Connected Components

---

### Pattern 7.3 — Union-Find

```java
class UF {
    int[] p, r;
    UF(int n) {
        p = new int[n]; r = new int[n];
        for (int i = 0; i < n; i++) p[i] = i;
    }
    int find(int x) { return p[x] == x ? x : (p[x] = find(p[x])); }
    boolean unite(int x, int y) {
        x = find(x); y = find(y);
        if (x == y) return false;
        if (r[x] < r[y]) { int t = x; x = y; y = t; }
        p[y] = x; if (r[x] == r[y]) r[x]++;
        return true;
    }
}
```
**LC:** 684 Redundant Connection, 721 Accounts Merge, 1584 Min Cost Connect Points

---

### Pattern 7.4 — Topological Sort (Kahn's)

```java
int[] indeg = new int[n];
for (int[] e : edges) { adj.get(e[0]).add(e[1]); indeg[e[1]]++; }
Queue<Integer> q = new LinkedList<>();
for (int i = 0; i < n; i++) if (indeg[i] == 0) q.offer(i);
List<Integer> order = new ArrayList<>();
while (!q.isEmpty()) {
    int u = q.poll(); order.add(u);
    for (int v : adj.get(u)) if (--indeg[v] == 0) q.offer(v);
}
return order.size() == n; // false = cycle
```
**LC:** 207 Course Schedule, 210 Course Schedule II, 269 Alien Dictionary

---

### Pattern 7.5 — Dijkstra

```java
int[] dist = new int[n];
Arrays.fill(dist, Integer.MAX_VALUE);
PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[0]));
dist[src] = 0; pq.offer(new int[]{0, src});
while (!pq.isEmpty()) {
    int[] curr = pq.poll();
    int d = curr[0], u = curr[1];
    if (d > dist[u]) continue;
    for (int[] edge : adj.get(u)) {
        int v = edge[0], w = edge[1];
        if (dist[u] + w < dist[v]) {
            dist[v] = dist[u] + w;
            pq.offer(new int[]{dist[v], v});
        }
    }
}
```
**LC:** 743 Network Delay Time, 787 Cheapest Flights K Stops, 1631 Min Effort Path

---

### Pattern 7.6 — Cycle Detection (Directed — 3 Colors)

```java
// 0=unvisited  1=in-stack  2=done
boolean dfs(int u) {
    color[u] = 1;
    for (int v : adj.get(u)) {
        if (color[v] == 1) return true;  // back edge = cycle
        if (color[v] == 0 && dfs(v)) return true;
    }
    color[u] = 2;
    return false;
}
```

---

### Pattern 7.7 — Multi-Source BFS

> Push **all sources at step 0**. BFS naturally finds minimum distance from any source simultaneously.

**LC:** 994 Rotting Oranges, 286 Walls and Gates, 1162 As Far From Land As Possible

---

## 8. DYNAMIC PROGRAMMING

### Pattern 8.1 — 1D Linear DP

```java
int[] dp = new int[n];
dp[0] = base;
for (int i = 1; i < n; i++)
    dp[i] = Math.max(dp[i - 1] + nums[i], nums[i]); // example: max subarray
```
**LC:** 70 Climbing Stairs, 198 House Robber, 53 Max Subarray (Kadane's), 300 LIS

---

### Pattern 8.2 — Kadane's

```java
int cur = nums[0], best = nums[0];
for (int i = 1; i < n; i++) {
    cur = Math.max(nums[i], cur + nums[i]);
    best = Math.max(best, cur);
}
```
**LC:** 53, 152 Max Product Subarray (track min too)

---

### Pattern 8.3 — 2D DP (Grid / Two Sequences)

```java
int[][] dp = new int[m + 1][n + 1];
for (int i = 1; i <= m; i++)
    for (int j = 1; j <= n; j++)
        dp[i][j] = (A[i-1] == B[j-1]) ? dp[i-1][j-1] + 1
                                        : Math.max(dp[i-1][j], dp[i][j-1]);
```
**LC:** 62 Unique Paths, 64 Min Path Sum, 1143 LCS, 72 Edit Distance

---

### Pattern 8.4 — 0/1 Knapsack

```java
int[] dp = new int[W + 1];
for (int[] item : items) {
    int w = item[0], v = item[1];
    for (int c = W; c >= w; c--)  // BACKWARDS = each item once
        dp[c] = Math.max(dp[c], dp[c - w] + v);
}
```
**LC:** 416 Partition Equal Subset Sum, 494 Target Sum

---

### Pattern 8.5 — Unbounded Knapsack (Coin Change)

```java
int[] dp = new int[amount + 1];
Arrays.fill(dp, Integer.MAX_VALUE);
dp[0] = 0;
for (int coin : coins)
    for (int c = coin; c <= amount; c++)  // FORWARDS = reuse allowed
        if (dp[c - coin] != Integer.MAX_VALUE)
            dp[c] = Math.min(dp[c], dp[c - coin] + 1);
```
**LC:** 322 Coin Change, 518 Coin Change II

---

### Pattern 8.6 — Interval DP

```java
for (int len = 2; len <= n; len++)
    for (int i = 0; i <= n - len; i++) {
        int j = i + len - 1;
        for (int k = i; k < j; k++)
            dp[i][j] = Math.max(dp[i][j], dp[i][k] + dp[k + 1][j] + cost);
    }
```
**LC:** 312 Burst Balloons, 516 Longest Palindromic Subsequence

---

### Pattern 8.7 — State Machine DP (Stocks)

```java
int hold = Integer.MIN_VALUE, sold = 0, rest = 0;
for (int p : prices) {
    int pSold = sold;
    sold = hold + p;
    hold = Math.max(hold, rest - p);
    rest = Math.max(rest, pSold);
}
return Math.max(sold, rest);
```
**LC:** 121 / 122 / 123 / 188 / 309 / 714 (all stock variants)

---

### Pattern 8.8 — Memoized DFS (Top-Down)

```java
Map<Integer, Integer> memo = new HashMap<>();
int dp(int i) {
    if (i >= n) return 0;
    if (memo.containsKey(i)) return memo.get(i);
    int res = Math.max(dp(i + 1), nums[i] + dp(i + 2));
    memo.put(i, res);
    return res;
}
```
**LC:** 198 House Robber, 139 Word Break, 329 LIP in Matrix

---

### Pattern 8.9 — Bitmask DP

```java
int[] dp = new int[1 << n];
Arrays.fill(dp, Integer.MAX_VALUE);
dp[0] = 0;
for (int mask = 0; mask < (1 << n); mask++) {
    if (dp[mask] == Integer.MAX_VALUE) continue;
    int pos = Integer.bitCount(mask);
    for (int j = 0; j < n; j++) {
        if ((mask >> j & 1) != 0) continue;
        int next = mask | (1 << j);
        dp[next] = Math.min(dp[next], dp[mask] + cost[pos][j]);
    }
}
return dp[(1 << n) - 1];
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

```java
Arrays.sort(intervals, (a, b) -> a[0] - b[0]);
List<int[]> res = new ArrayList<>();
res.add(intervals[0]);
for (int[] iv : intervals) {
    int[] last = res.get(res.size() - 1);
    if (iv[0] <= last[1])
        last[1] = Math.max(last[1], iv[1]); // merge
    else
        res.add(iv);
}
```
**LC:** 56 Merge Intervals, 57 Insert Interval

---

### Pattern 9.2 — Non-Overlapping (Greedy Removal)
**Trigger:** minimum removals to make non-overlapping

```java
Arrays.sort(intervals, (a, b) -> a[1] - b[1]); // sort by END time — greedy keep earliest-ending
int count = 0, end = Integer.MIN_VALUE;
for (int[] iv : intervals) {
    if (iv[0] >= end) end = iv[1];  // no overlap, keep
    else              count++;       // overlap, remove current
}
return count;
```
**LC:** 435 Non-Overlapping Intervals, 452 Minimum Arrows to Burst Balloons

---

### Pattern 9.3 — Meeting Rooms (Simultaneous Intervals)

```java
Arrays.sort(intervals, (a, b) -> a[0] - b[0]);
PriorityQueue<Integer> ends = new PriorityQueue<>(); // min-heap of end times
for (int[] iv : intervals) {
    if (!ends.isEmpty() && ends.peek() <= iv[0])
        ends.poll();  // free a room
    ends.offer(iv[1]);
}
return ends.size(); // rooms needed
```
**LC:** 253 Meeting Rooms II, 252 Meeting Rooms I

---

## 10. LINKED LIST

### Pattern 10.1 — Reverse Linked List

```java
ListNode prev = null, cur = head;
while (cur != null) {
    ListNode next = cur.next;
    cur.next = prev;
    prev = cur; cur = next;
}
return prev;
```
**LC:** 206, 92 Reverse Between (partial), 25 Reverse K Group

---

### Pattern 10.2 — Find Cycle Start

```java
ListNode slow = head, fast = head;
while (fast != null && fast.next != null) {
    slow = slow.next; fast = fast.next.next;
    if (slow == fast) {
        slow = head;
        while (slow != fast) { slow = slow.next; fast = fast.next; }
        return slow; // cycle start
    }
}
return null;
```
**LC:** 142 Linked List Cycle II

---

### Pattern 10.3 — Merge Two Sorted Lists

```java
ListNode dummy = new ListNode(0), cur = dummy;
while (l1 != null && l2 != null) {
    if (l1.val <= l2.val) { cur.next = l1; l1 = l1.next; }
    else                  { cur.next = l2; l2 = l2.next; }
    cur = cur.next;
}
cur.next = (l1 != null) ? l1 : l2;
return dummy.next;
```
**LC:** 21, 23 Merge K Sorted (use min-heap)

---

## 11. STACK & MONOTONIC STACK

**Mental Model**
A monotonic stack maintains elements in increasing or decreasing order. When you push a new element that violates the order, you pop until the order is restored — **each pop is your "answer" for the popped element**.

---

### Pattern 11.1 — Next Greater Element

```java
Deque<Integer> st = new ArrayDeque<>();
int[] res = new int[n];
Arrays.fill(res, -1);
for (int i = 0; i < n; i++) {
    while (!st.isEmpty() && nums[st.peek()] < nums[i]) {
        res[st.pop()] = nums[i]; // i is the next greater for st.top()
    }
    st.push(i);
}
```
**LC:** 496 Next Greater Element, 739 Daily Temperatures, 503 Circular Next Greater

---

### Pattern 11.2 — Largest Rectangle in Histogram

```java
Deque<Integer> st = new ArrayDeque<>();
int maxArea = 0;
int[] newNums = Arrays.copyOf(nums, nums.length + 1); // sentinel 0
for (int i = 0; i <= n; i++) {
    while (!st.isEmpty() && newNums[st.peek()] > newNums[i]) {
        int h = newNums[st.pop()];
        int w = st.isEmpty() ? i : i - st.peek() - 1;
        maxArea = Math.max(maxArea, h * w);
    }
    st.push(i);
}
```
**LC:** 84 Largest Rectangle in Histogram, 85 Maximal Rectangle

---

### Pattern 11.3 — Valid Parentheses / Matching

```java
Deque<Character> st = new ArrayDeque<>();
for (char c : s.toCharArray()) {
    if (c == '(' || c == '[' || c == '{') st.push(c);
    else {
        if (st.isEmpty()) return false;
        if (c == ')' && st.peek() != '(') return false;
        // etc.
        st.pop();
    }
}
return st.isEmpty();
```
**LC:** 20 Valid Parentheses, 394 Decode String, 1249 Minimum Remove to Make Valid

---

## 12. HEAP / PRIORITY QUEUE

### Pattern 12.1 — Two Heaps (Median of Stream)

```java
PriorityQueue<Integer> lo = new PriorityQueue<>(Collections.reverseOrder()); // max-heap: lower half
PriorityQueue<Integer> hi = new PriorityQueue<>();                            // min-heap: upper half

void addNum(int num) {
    lo.offer(num);
    hi.offer(lo.poll());        // send max of lo to hi
    if (hi.size() > lo.size()) {
        lo.offer(hi.poll());    // keep lo.size() >= hi.size()
    }
}
double findMedian() {
    return lo.size() > hi.size() ? lo.peek()
                                 : (lo.peek() + hi.peek()) / 2.0;
}
```
**LC:** 295 Find Median from Data Stream, 480 Sliding Window Median

---

### Pattern 12.2 — Merge K Sorted Lists / K-Way Merge

```java
PriorityQueue<ListNode> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a.val));
for (ListNode l : lists) if (l != null) pq.offer(l);
ListNode dummy = new ListNode(0), cur = dummy;
while (!pq.isEmpty()) {
    ListNode node = pq.poll();
    cur.next = node; cur = node;
    if (node.next != null) pq.offer(node.next);
}
return dummy.next;
```
**LC:** 23 Merge K Sorted Lists, 378 Kth Smallest in Sorted Matrix

---

## 13. HASHING

### Pattern 13.1 — Two Sum (HashMap)

```java
Map<Integer, Integer> seen = new HashMap<>();
for (int i = 0; i < n; i++) {
    int comp = target - nums[i];
    if (seen.containsKey(comp)) return new int[]{seen.get(comp), i};
    seen.put(nums[i], i);
}
return new int[]{};
```
**LC:** 1 Two Sum, 167 Two Sum II, 170 Two Sum III

---

### Pattern 13.2 — Frequency Map

```java
Map<Integer, Integer> freq = new HashMap<>();
for (int x : nums) freq.merge(x, 1, Integer::sum);
```
**LC:** 347 Top K Frequent, 242 Valid Anagram, 49 Group Anagrams

---

### Pattern 13.3 — Prefix Sum + HashMap
**Trigger:** "subarray sum equals K", "number of subarrays with sum divisible by K"

```java
Map<Integer, Integer> count = new HashMap<>();
count.put(0, 1);
int sum = 0, result = 0;
for (int x : nums) {
    sum += x;
    result += count.getOrDefault(sum - k, 0);  // if sum-k seen before, subarray exists
    count.merge(sum, 1, Integer::sum);
}
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
| nth_element | O(N) avg | O(1) |
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
             ├─ Sparse / irregular states             → Memoized DFS
             └─ N ≤ 20, subsets                       → Bitmask DP
```

---

## 🏆 THE FIVE INTERVIEW RULES

1. **State complexity before coding** — shows you understand what you're building
2. **Name the pattern out loud** — "this is a sliding window" signals confidence
3. **Edge cases first** — empty, single element, all same, negative numbers, overflow
4. **Walk one example after coding** — catch off-by-ones before the interviewer does
