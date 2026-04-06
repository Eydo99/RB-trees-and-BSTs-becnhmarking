import pandas as pd
import matplotlib.pyplot as plt
import numpy as np

# Read CSV
df = pd.read_csv('results.csv')

distributions = df['Distribution'].unique()
operations = ['Insert', 'Contains', 'Delete', 'Sort']

# ── 1. PRINT TABLES ──────────────────────────────────────────────────────────
for dist in distributions:
    print(f"\n{'='*60}")
    print(f"Distribution: {dist}")
    print(f"{'='*60}")
    subset = df[df['Distribution'] == dist]
    print(subset[['Structure', 'Operation', 'Mean', 'Median', 'StdDev']].to_string(index=False))

# ── 2. BAR CHARTS per operation ───────────────────────────────────────────────
fig, axes = plt.subplots(2, 2, figsize=(14, 10))
fig.suptitle('BST vs RBTree Performance Comparison', fontsize=16, fontweight='bold')

axes = axes.flatten()

for idx, operation in enumerate(operations):
    ax = axes[idx]
    op_data = df[df['Operation'] == operation]

    bst_means = []
    rbt_means = []
    dist_labels = []

    for dist in distributions:
        dist_data = op_data[op_data['Distribution'] == dist]
        bst_row = dist_data[dist_data['Structure'] == 'BST']
        rbt_row = dist_data[dist_data['Structure'] == 'RBTree']

        if not bst_row.empty and not rbt_row.empty:
            bst_means.append(bst_row['Mean'].values[0])
            rbt_means.append(rbt_row['Mean'].values[0])
            # shorten label for readability
            label = dist.replace('Nearly-Sorted with ', 'NS-').replace(' misplaced elements', '')
            dist_labels.append(label)

    x = np.arange(len(dist_labels))
    width = 0.35

    bars1 = ax.bar(x - width/2, bst_means, width, label='BST', color='steelblue', alpha=0.8)
    bars2 = ax.bar(x + width/2, rbt_means, width, label='RBTree', color='tomato', alpha=0.8)

    ax.set_title(f'{operation} Operation', fontweight='bold')
    ax.set_ylabel('Mean Time (ms)')
    ax.set_xticks(x)
    ax.set_xticklabels(dist_labels, rotation=15, ha='right', fontsize=8)
    ax.legend()
    ax.grid(axis='y', alpha=0.3)

    # add value labels on bars
    for bar in bars1:
        ax.text(bar.get_x() + bar.get_width()/2, bar.get_height(),
                f'{bar.get_height():.1f}', ha='center', va='bottom', fontsize=7)
    for bar in bars2:
        ax.text(bar.get_x() + bar.get_width()/2, bar.get_height(),
                f'{bar.get_height():.1f}', ha='center', va='bottom', fontsize=7)

plt.tight_layout()
plt.savefig('bst_vs_rbtree.png', dpi=150, bbox_inches='tight')

print("\nPlot saved as bst_vs_rbtree.png")

# ── 3. SORT COMPARISON (BST vs RBTree vs MergeSort) ───────────────────────────
fig2, ax2 = plt.subplots(figsize=(12, 6))
fig2.suptitle('Sort Comparison: BST vs RBTree vs MergeSort', fontsize=14, fontweight='bold')

sort_data = df[df['Operation'] == 'Sort']
bst_sort = []
rbt_sort = []
merge_sort = []
labels = []

for dist in distributions:
    dist_data = sort_data[sort_data['Distribution'] == dist]
    bst_row = dist_data[dist_data['Structure'] == 'BST']
    rbt_row = dist_data[dist_data['Structure'] == 'RBTree']
    merge_row = dist_data[dist_data['Structure'] == 'MergeSort']

    if not bst_row.empty:
        bst_sort.append(bst_row['Mean'].values[0])
    if not rbt_row.empty:
        rbt_sort.append(rbt_row['Mean'].values[0])
    if not merge_row.empty:
        merge_sort.append(merge_row['Mean'].values[0])

    label = dist.replace('Nearly-Sorted with ', 'NS-').replace(' misplaced elements', '')
    labels.append(label)

x = np.arange(len(labels))
width = 0.25

ax2.bar(x - width, bst_sort, width, label='BST Sort', color='steelblue', alpha=0.8)
ax2.bar(x, rbt_sort, width, label='RBTree Sort', color='tomato', alpha=0.8)
ax2.bar(x + width, merge_sort, width, label='MergeSort', color='green', alpha=0.8)

ax2.set_ylabel('Mean Time (ms)')
ax2.set_xticks(x)
ax2.set_xticklabels(labels, rotation=15, ha='right')
ax2.legend()
ax2.grid(axis='y', alpha=0.3)

plt.tight_layout()
plt.savefig('sort_comparison.png', dpi=150, bbox_inches='tight')
print("Sort comparison plot saved as sort_comparison.png")
plt.show()