import json
import os
import camelot
import pandas as pd
import numpy as np

def merge_tables(tables):
    """
    Merges tables that are split across pages if they have the same column count.
    """
    if not tables:
        return []
    merged_tables = []
    if not tables:
        return merged_tables
    current_df = tables[0].df
    for i in range(1, len(tables)):
        next_df = tables[i].df
        if current_df.shape[1] == next_df.shape[1]:
            if all(isinstance(c, int) for c in next_df.columns):
                next_df.columns = current_df.columns
            if list(current_df.columns) == list(next_df.columns):
                 current_df = pd.concat([current_df, next_df], ignore_index=True)
            else:
                merged_tables.append(current_df)
                current_df = next_df
        else:
            merged_tables.append(current_df)
            current_df = next_df
    merged_tables.append(current_df)
    return merged_tables

def clean_cell_overflow(df):
    """
    Cleans a merged dataframe by combining rows where cell content has spilled over.
    A row is considered a continuation if its first column is empty.
    """
    df.replace('', np.nan, inplace=True)
    df.dropna(how='all', inplace=True)
    df.reset_index(drop=True, inplace=True)
    if df.empty:
        return df

    pk_col = df.columns[0]
    rows_to_drop = []
    for i in range(len(df) - 1, 0, -1):
        current_row = df.iloc[i]
        if pd.isnull(current_row[pk_col]):
            for j in range(i - 1, -1, -1):
                if j not in rows_to_drop:
                    prev_row_index = j
                    break
            else:
                continue
            for col in df.columns:
                if not pd.isnull(current_row[col]):
                    prev_val = df.at[prev_row_index, col]
                    new_val = str(prev_val) + '\n' + str(current_row[col]) if not pd.isnull(prev_val) else current_row[col]
                    df.at[prev_row_index, col] = new_val
            rows_to_drop.append(i)
    df.drop(rows_to_drop, inplace=True)
    df.reset_index(drop=True, inplace=True)
    df.fillna('', inplace=True)
    return df

def heuristic_spill_merge(dfs):
    """
    Merges spill-over text between tables that have different column counts.
    """
    if len(dfs) < 2:
        return dfs

    processed_dfs = [df.copy() for df in dfs]
    indices_to_remove = set()

    for i in range(len(processed_dfs) - 1):
        df_a = processed_dfs[i]
        df_b = processed_dfs[i+1]

        if df_a.empty or df_b.empty or i in indices_to_remove:
            continue

        first_row_b = df_b.iloc[0]
        non_null_count = first_row_b.replace('', np.nan).count()

        if 1 <= non_null_count <= 2:
            spill_over_text = ' '.join(first_row_b.dropna().astype(str))
            last_row_a = df_a.index[-1]
            last_col_a = df_a.columns[-1]
            df_a.at[last_row_a, last_col_a] = str(df_a.at[last_row_a, last_col_a]) + ' ' + spill_over_text
            
            processed_dfs[i+1] = df_b.drop(df_b.index[0]).reset_index(drop=True)
            if processed_dfs[i+1].empty:
                indices_to_remove.add(i+1)

    final_dfs = [df for idx, df in enumerate(processed_dfs) if idx not in indices_to_remove]
    return final_dfs

# --- Main script execution ---

SRC_FILE = "M1-SDP-PRO-SRS_v0.1.0.pdf"
# *** CHANGE: Using flavor='lattice' to handle tables with explicit grid lines ***
tables = camelot.read_pdf(SRC_FILE, pages="1-end", flavor='lattice',
                          split_text=True, flag_size=True, line_scale=40)

print(f"Extracted {len(tables)} table fragments initially using Lattice method.")

# 1. Merge tables with same column count
merged_dfs = merge_tables(tables)
print(f"Merged into {len(merged_dfs)} tables based on column count.")

# 2. Clean cell overflows within each table
cleaned_dfs = [clean_cell_overflow(df.copy()) for df in merged_dfs]
print("Cleaned cell overflows within tables.")

# 3. Perform heuristic merge for spill-over text between tables
final_dfs = heuristic_spill_merge(cleaned_dfs)
print(f"Heuristically merged spill-over text, resulting in {len(final_dfs)} final tables.")

# 4. Save the final tables
output_dir = "Exs_cleaned_final_v4"
if not os.path.exists(output_dir):
    os.makedirs(output_dir)

for i, df in enumerate(final_dfs):
    if not df.empty and len(df.columns) > 0:
        df = df[df[df.columns[0]] != df.columns[0]]

    print(f"Saving Final Table {i} with shape {df.shape}")
    df.to_csv(f"{output_dir}/table_{i}.csv", index=False)
    df.to_json(f"{output_dir}/table_{i}.json", orient="records", indent=2)

print(f"Final tables saved in '{output_dir}' directory.")
