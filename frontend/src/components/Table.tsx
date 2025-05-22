import DataTable, {type TableColumn } from 'react-data-table-component';

export interface GenericTableProps<T> {
    title: string;
    columns: TableColumn<T>[];
    data: T[];
    loading: boolean;
    isError: boolean;
    onRowSelected: (state: { selectedRows: T[] }) => void;
    theme: 'light' | 'dark';
}

function GenericTable<T>({
                             title,
                             columns,
                             data,
                             loading,
                             isError,
                             onRowSelected,
                             theme,
                         }: GenericTableProps<T>) {
    return (
        <>
            {loading ? (
                <p className="loading-text">Loading...</p>
            ) : isError ? (
                <p className="error-text">An error occurred while fetching data</p>
            ) : (
                <div className="table-container">
                    <DataTable
                        title={title}
                        columns={columns}
                        data={data}
                        pagination
                        highlightOnHover
                        selectableRows
                        onSelectedRowsChange={onRowSelected}
                        theme={theme === 'dark' ? 'dark' : 'default'}
                    />
                </div>
            )}
        </>
    );
}

export default GenericTable;
