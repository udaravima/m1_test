import json
import os
import camelot
import pandas as pd
import numpy as np


def merge_tables(tables):
    merge_tables = []
    if not tables:
        return []

    current_df = tables[0].df
    for i in range(1, len(tables)):
        next_df = tables[i].df
        if (current_df.shape[1] == next_df.shape[1]):
            current_df = pd.concat([current_df, next_df], ignore_index=True)
        else:
            merge_tables.append(current_df)
            current_df = next_df
    merge_tables.append(current_df)
    return merge_tables


def main():
    SRC_ROOT = "Documents/"
    SRC_FILE = f"{SRC_ROOT}M1-SDP-PRO-SRS_v0.1.0_annotated.pdf"
    # *** CHANGE: Using flavor='lattice' to handle tables with explicit grid lines ***
    tables = camelot.read_pdf(SRC_FILE, pages="1-end", flavor='lattice',
                              split_text=False, flag_size=True)

    print(
        f"Extracted {len(tables)} table fragments initially using Lattice method.")

    # 1. Merge tables with same column count
    merged_dfs = merge_tables(tables)
    print(f"Merged into {len(merged_dfs)} tables based on column count.")

    # 4. Save the final tables
    output_dir = f"{SRC_ROOT}SRS_Fields"

    if not os.path.exists(output_dir):
        os.makedirs(output_dir)
        os.makedirs(f"{output_dir}/json")
        os.makedirs(f"{output_dir}/csv")

    for i, df in enumerate(merged_dfs):
        print(f"Table {i} shape: {df.shape}")
        if not df.empty:
            df.to_json(
                f"{output_dir}/json/table_{i}.json", orient="records")
            df.to_csv(f"{output_dir}/csv/table_{i}.csv", index=False)

    print(
        f"Final tables saved in '{output_dir}/json' and '{output_dir}/csv' directory.")


if __name__ == "__main__":
    main()
